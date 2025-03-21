package inote.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import inote.entity.Note;
import inote.repository.NoteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для NoteController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.yaml") // Указываем путь к файлу конфигурации для тестов
public class NoteControllerIT {

    private static final String BASE_URL = "/inote/notes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Note testNote;

    @BeforeEach
    void setUp() {
        noteRepository.findAll().forEach(note -> noteRepository.deleteById(note.getId()));

        testNote = Note.builder()
            .title("Test Note")
            .content("Test Content")
            .createdAt(LocalDateTime.now())
            .build();

        testNote = noteRepository.save(testNote);
    }

    @Test
    @Order(1)
    @DisplayName("Получение всех заметок")
    void testGetAllNotes() throws Exception {
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value(testNote.getTitle()));
    }

    @Test
    @Order(2)
    @DisplayName("Получение заметки по ID")
    void testGetNoteById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + testNote.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testNote.getId()))
            .andExpect(jsonPath("$.title").value(testNote.getTitle()));
    }

    @Test
    @Order(3)
    @DisplayName("Получение заметки по несуществующему ID")
    void testGetNoteByNonExistentId() throws Exception {
        mockMvc.perform(get(BASE_URL + "/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("Создание новой заметки")
    void testCreateNote() throws Exception {
        Note newNote = Note.builder()
            .title("New Note")
            .content("New Content")
            .createdAt(LocalDateTime.now())
            .build();

        String response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNote)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Note createdNote = objectMapper.readValue(response, Note.class);

        assertThat(createdNote.getId()).isNotNull();
        assertThat(createdNote.getTitle()).isEqualTo("New Note");
    }

    @Test
    @Order(5)
    @DisplayName("Обновление существующей заметки")
    void testUpdateNote() throws Exception {
        Note updatedNote = Note.builder()
            .title("Updated Title")
            .content("Updated Content")
            .build();

        mockMvc.perform(put(BASE_URL + "/" + testNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedNote)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    @Order(6)
    @DisplayName("Удаление существующей заметки")
    void testDeleteNote() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + testNote.getId()))
            .andExpect(status().isNoContent());

        Optional<Note> deletedNote = noteRepository.findById(testNote.getId());
        assertThat(deletedNote).isEmpty();
    }
}
