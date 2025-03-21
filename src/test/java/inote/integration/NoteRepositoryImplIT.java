package inote.integration;

import inote.entity.Note;
import inote.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.yaml")
class NoteRepositoryImplIT {

    @Autowired
    private NoteRepository noteRepository;

    private Note note;

    @BeforeEach
    void setUp() {
        // Очистим базу данных перед каждым тестом
        noteRepository.findAll().forEach(n -> noteRepository.deleteById(n.getId()));

        // Given: создаем заметку для тестов
        note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note.");
        note.setCreatedAt(LocalDateTime.now());
        note = noteRepository.save(note);
    }

    @Test
    void testFindById() {
        // Given: есть заметка, сохраненная в базе

        // When: ищем заметку по ее ID
        Optional<Note> foundNote = noteRepository.findById(note.getId());

        // Then: проверяем, что заметка найдена и ее ID и заголовок соответствуют ожидаемым
        assertThat(foundNote).isPresent();
        assertThat(foundNote.get().getId()).isEqualTo(note.getId());
        assertThat(foundNote.get().getTitle()).isEqualTo("Test Note");
    }

    @Test
    void testFindByTitle() {
        // Given: есть заметка с заголовком "Test Note"

        // When: ищем заметку по заголовку
        List<Note> notes = noteRepository.findByTitle("Test Note");

        // Then: проверяем, что заметка найдена и заголовок соответствует ожидаемому
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getTitle()).isEqualTo("Test Note");
    }

    @Test
    void testFindByCreatedAtBetween() {
        // Given: есть заметка с датой создания в пределах последнего дня

        // When: ищем заметки, созданные между двумя датами
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Note> notes = noteRepository.findByCreatedAtBetween(startDate, endDate);

        // Then: проверяем, что найденная заметка создана в ожидаемом диапазоне
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getCreatedAt()).isBetween(startDate, endDate);
    }

    @Test
    void testSave() {
        // Given: новая заметка для сохранения
        Note newNote = new Note();
        newNote.setTitle("New Note");
        newNote.setContent("This is a new note.");
        newNote.setCreatedAt(LocalDateTime.now());

        // When: сохраняем новую заметку
        Note savedNote = noteRepository.save(newNote);

        // Then: проверяем, что заметка сохранена, и у неё есть ID
        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getId()).isNotNull();
        assertThat(savedNote.getTitle()).isEqualTo("New Note");
    }

    @Test
    void testUpdate() {
        // Given: есть заметка с ID и мы хотим обновить её данные
        note.setTitle("Updated Title");
        note.setContent("Updated content");

        // When: сохраняем обновленную заметку
        Note updatedNote = noteRepository.save(note);

        // Then: проверяем, что заметка обновилась
        assertThat(updatedNote.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedNote.getContent()).isEqualTo("Updated content");
    }

    @Test
    void testDeleteById() {
        // Given: есть заметка с ID, которую мы хотим удалить
        Long noteId = note.getId();

        // When: удаляем заметку по ID
        noteRepository.deleteById(noteId);

        // Then: проверяем, что заметка была удалена
        Optional<Note> deletedNote = noteRepository.findById(noteId);
        assertThat(deletedNote).isEmpty();
    }
}
