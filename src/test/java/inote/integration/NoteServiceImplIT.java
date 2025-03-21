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
@Import(NoteServiceImpl.class) // Подключаем сервис
@TestPropertySource("classpath:application-test.yaml") // Указываем путь к файлу конфигурации для тестов
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
        List<Note> notes = noteService.findAll();
        assertEquals(2, notes.size());
    }

    @Test
    void findById_ShouldReturnNote_WhenIdExists() {
        Optional<Note> foundNote = noteService.findById(note1.getId());
        assertTrue(foundNote.isPresent());
        assertEquals(note1.getTitle(), foundNote.get().getTitle());
    }

    @Test
    void findById_ShouldThrowException_WhenIdDoesNotExist() {
        assertThrows(NotFoundException.class, () -> noteService.findById(999L));
    }

    @Test
    void findByTitle_ShouldReturnNotes_WhenTitleMatches() {
        List<Note> notes = noteService.findByTitle("First Note");
        assertEquals(1, notes.size());
        assertEquals("First Note", notes.get(0).getTitle());
    }

    @Test
    void findByTitle_ShouldThrowException_WhenNoMatch() {
        assertThrows(NotFoundException.class, () -> noteService.findByTitle("Nonexistent Title"));
    }

    @Test
    void findByCreatedAtBetween_ShouldReturnNotes_WhenWithinRange() {
        List<Note> notes = noteService.findByCreatedAtBetween(
            LocalDateTime.now().minusDays(2), LocalDateTime.now()
        );
        assertEquals(2, notes.size());
    }

    @Test
    void save_ShouldPersistNote() {
        Note newNote = Note.builder()
            .title("New Note")
            .content("New content")
            .createdAt(LocalDateTime.now())
            .build();

        Note savedNote = noteService.save(newNote);
        assertNotNull(savedNote.getId());
        assertEquals("New Note", savedNote.getTitle());
    }

    @Test
    void update_ShouldModifyExistingNote() {
        Note updatedNote = Note.builder()
            .title("Updated Note")
            .content("Updated content")
            .createdAt(note1.getCreatedAt())
            .build();

        Optional<Note> result = noteService.update(note1.getId(), updatedNote);
        assertTrue(result.isPresent());
        assertEquals("Updated Note", result.get().getTitle());
    }

    @Test
    void update_ShouldThrowException_WhenNoteDoesNotExist() {
        Note nonExistentNote = Note.builder().title("Fake Note").content("Fake").build();
        assertThrows(NotFoundException.class, () -> noteService.update(999L, nonExistentNote));
    }

    @Test
    void deleteById_ShouldRemoveNote_WhenIdExists() {
        noteService.deleteById(note1.getId());
        assertFalse(noteRepository.findById(note1.getId()).isPresent());
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdDoesNotExist() {
        assertThrows(NotFoundException.class, () -> noteService.deleteById(999L));
    }
}
