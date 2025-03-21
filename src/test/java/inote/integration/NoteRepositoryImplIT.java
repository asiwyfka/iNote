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

        // Создание заметки для тестов
        note = new Note();
        note.setTitle("Test Note");
        note.setContent("This is a test note.");
        note.setCreatedAt(LocalDateTime.now());
        note = noteRepository.save(note);
    }

    @Test
    void testFindById() {
        // Тест на поиск по ID
        Optional<Note> foundNote = noteRepository.findById(note.getId());
        assertThat(foundNote).isPresent();
        assertThat(foundNote.get().getId()).isEqualTo(note.getId());
        assertThat(foundNote.get().getTitle()).isEqualTo("Test Note");
    }

    @Test
    void testFindByTitle() {
        // Тест на поиск по заголовку
        List<Note> notes = noteRepository.findByTitle("Test Note");
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getTitle()).isEqualTo("Test Note");
    }

    @Test
    void testFindByCreatedAtBetween() {
        // Тест на поиск заметок по дате
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Note> notes = noteRepository.findByCreatedAtBetween(startDate, endDate);
        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getCreatedAt()).isBetween(startDate, endDate);
    }

    @Test
    void testSave() {
        // Тест на сохранение новой заметки
        Note newNote = new Note();
        newNote.setTitle("New Note");
        newNote.setContent("This is a new note.");
        newNote.setCreatedAt(LocalDateTime.now());
        Note savedNote = noteRepository.save(newNote);
        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getId()).isNotNull();
        assertThat(savedNote.getTitle()).isEqualTo("New Note");
    }

    @Test
    void testUpdate() {
        // Тест на обновление заметки
        note.setTitle("Updated Title");
        note.setContent("Updated content");
        Note updatedNote = noteRepository.save(note);
        assertThat(updatedNote.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedNote.getContent()).isEqualTo("Updated content");
    }

    @Test
    void testDeleteById() {
        // Тест на удаление заметки по ID
        Long noteId = note.getId();
        noteRepository.deleteById(noteId);
        Optional<Note> deletedNote = noteRepository.findById(noteId);
        assertThat(deletedNote).isEmpty();
    }
}
