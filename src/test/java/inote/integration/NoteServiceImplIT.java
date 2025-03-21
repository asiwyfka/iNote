package inote.integration;

import inote.entity.Note;
import inote.exception.NotFoundException;
import inote.repository.NoteRepository;
import inote.service.NoteService;
import inote.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(NoteServiceImpl.class)
@TestPropertySource("classpath:application-test.yaml")
class NoteServiceImplIT {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteService noteService;

    private Note note1, note2;

    @BeforeEach
    void setUp() {

        // Очищаем все записи перед каждым тестом
        noteRepository.findAll().forEach(note -> noteRepository.deleteById(note.getId()));

        // Given: создаем тестовые заметки
        note1 = noteRepository.save(
            Note.builder()
                .title("First Note")
                .content("Content of the first note")
                .createdAt(LocalDateTime.now())
                .build()
        );

        note2 = noteRepository.save(
            Note.builder()
                .title("Second Note")
                .content("Content of the second note")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build()
        );
    }

    @Test
    void findAll_ShouldReturnAllNotes() {
        // Given: подготовка данных уже выполнена в setUp()

        // When: запрашиваем все заметки
        List<Note> notes = noteService.findAll();

        // Then: проверяем, что возвращаются все заметки
        assertEquals(2, notes.size());
    }

    @Test
    void findById_ShouldReturnNote_WhenIdExists() {
        // Given: подготовлены заметки, и у нас есть note1 с известным ID

        // When: ищем заметку по ID
        Optional<Note> foundNote = noteService.findById(note1.getId());

        // Then: проверяем, что заметка найдена и её заголовок соответствует ожидаемому
        assertTrue(foundNote.isPresent());
        assertEquals(note1.getTitle(), foundNote.get().getTitle());
    }

    @Test
    void findById_ShouldThrowException_WhenIdDoesNotExist() {
        // Given: нет заметки с ID 999

        // When & Then: попытка найти заметку с несуществующим ID должна вызвать исключение
        assertThrows(NotFoundException.class, () -> noteService.findById(999L));
    }

    @Test
    void findByTitle_ShouldReturnNotes_WhenTitleMatches() {
        // Given: подготовлены заметки, и у нас есть note1 с заголовком "First Note"

        // When: ищем заметки по заголовку
        List<Note> notes = noteService.findByTitle("First Note");

        // Then: проверяем, что найдено 1 совпадение, и оно соответствует ожидаемому заголовку
        assertEquals(1, notes.size());
        assertEquals("First Note", notes.get(0).getTitle());
    }

    @Test
    void findByTitle_ShouldThrowException_WhenNoMatch() {
        // Given: нет заметок с заголовком "Nonexistent Title"

        // When & Then: попытка найти заметки с несуществующим заголовком должна вызвать исключение
        assertThrows(NotFoundException.class, () -> noteService.findByTitle("Nonexistent Title"));
    }

    @Test
    void findByCreatedAtBetween_ShouldReturnNotes_WhenWithinRange() {
        // Given: подготовлены две заметки с датами в пределах последнего 2 дней

        // When: ищем заметки, созданные за последние два дня
        List<Note> notes = noteService.findByCreatedAtBetween(
            LocalDateTime.now().minusDays(2), LocalDateTime.now()
        );

        // Then: проверяем, что найдено 2 заметки
        assertEquals(2, notes.size());
    }

    @Test
    void save_ShouldPersistNote() {
        // Given: создаем новую заметку
        Note newNote = Note.builder()
            .title("New Note")
            .content("New content")
            .createdAt(LocalDateTime.now())
            .build();

        // When: сохраняем новую заметку
        Note savedNote = noteService.save(newNote);

        // Then: проверяем, что заметка была сохранена, и у нее есть ID
        assertNotNull(savedNote.getId());
        assertEquals("New Note", savedNote.getTitle());
    }

    @Test
    void update_ShouldModifyExistingNote() {
        // Given: подготовлена заметка note1, которую будем обновлять
        Note updatedNote = Note.builder()
            .title("Updated Note")
            .content("Updated content")
            .createdAt(note1.getCreatedAt())
            .build();

        // When: обновляем заметку по ID
        Optional<Note> result = noteService.update(note1.getId(), updatedNote);

        // Then: проверяем, что заметка обновлена
        assertTrue(result.isPresent());
        assertEquals("Updated Note", result.get().getTitle());
    }

    @Test
    void update_ShouldThrowException_WhenNoteDoesNotExist() {
        // Given: нет заметки с ID 999

        // When & Then: попытка обновить несуществующую заметку должна вызвать исключение
        Note nonExistentNote = Note.builder().title("Fake Note").content("Fake").build();
        assertThrows(NotFoundException.class, () -> noteService.update(999L, nonExistentNote));
    }

    @Test
    void deleteById_ShouldRemoveNote_WhenIdExists() {
        // Given: подготовлена заметка с ID note1

        // When: удаляем заметку по ID
        noteService.deleteById(note1.getId());

        // Then: проверяем, что заметка удалена из репозитория
        assertFalse(noteRepository.findById(note1.getId()).isPresent());
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdDoesNotExist() {
        // Given: нет заметки с ID 999

        // When & Then: попытка удалить несуществующую заметку должна вызвать исключение
        assertThrows(NotFoundException.class, () -> noteService.deleteById(999L));
    }
}
