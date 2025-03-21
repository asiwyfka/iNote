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
@TestPropertySource("classpath:application-test.yaml")
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
        // Очищаем все записи перед каждым тестом
        noteRepository.findAll().forEach(note -> noteRepository.deleteById(note.getId()));

        // Given: создаем тестовую заметку
        testNote = Note.builder()
            .title("Test Note")
            .content("Test Content")
            .createdAt(LocalDateTime.now())
            .build();

        testNote = noteRepository.save(testNote);
    }
    @Test
    void testGetAllNotes() throws Exception {
        // Given: у нас уже есть одна заметка в базе данных, созданная в setUp()

        // When: мы выполняем GET-запрос на получение всех заметок
        mockMvc.perform(get(BASE_URL))
            // Then: ожидаем, что ответ будет успешным (200 OK)
            .andExpect(status().isOk())
            // Then: ожидаем, что количество заметок будет равно 1
            .andExpect(jsonPath("$.length()").value(1))
            // Then: ожидаем, что в ответе будет первая заметка с соответствующим title
            .andExpect(jsonPath("$[0].title").value(testNote.getTitle()));
    }

    @Test
    void testGetNoteById() throws Exception {
        // Given: у нас уже есть заметка с заданным id, созданная в setUp()

        // When: выполняем GET-запрос по ID этой заметки
        mockMvc.perform(get(BASE_URL + "/" + testNote.getId()))
            // Then: ожидаем, что статус ответа будет успешным (200 OK)
            .andExpect(status().isOk())
            // Then: проверяем, что в ответе будет корректный ID и title заметки
            .andExpect(jsonPath("$.id").value(testNote.getId()))
            .andExpect(jsonPath("$.title").value(testNote.getTitle()));
    }

    @Test
    void testGetNoteByNonExistentId() throws Exception {
        // Given: заметки с таким ID не существует в базе данных

        // When: выполняем GET-запрос с несуществующим ID
        mockMvc.perform(get(BASE_URL + "/99999"))
            // Then: ожидаем, что статус ответа будет 404 (Not Found)
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreateNote() throws Exception {
        // Given: у нас есть данные для новой заметки, которые хотим сохранить

        Note newNote = Note.builder()
            .title("New Note")
            .content("New Content")
            .createdAt(LocalDateTime.now())
            .build();

        // When: выполняем POST-запрос для создания новой заметки
        String response = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newNote)))
            // Then: ожидаем, что запрос завершится успешным статусом (200 OK)
            .andExpect(status().isOk())
            // Then: возвращаемая заметка должна иметь ID
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Then: проверяем, что новая заметка была создана с правильными данными
        Note createdNote = objectMapper.readValue(response, Note.class);
        assertThat(createdNote.getId()).isNotNull();
        assertThat(createdNote.getTitle()).isEqualTo("New Note");
    }

    @Test
    void testUpdateNote() throws Exception {
        // Given: у нас есть существующая заметка с тестовыми данными

        Note updatedNote = Note.builder()
            .title("Updated Title")
            .content("Updated Content")
            .build();

        // When: выполняем PUT-запрос на обновление заметки
        mockMvc.perform(put(BASE_URL + "/" + testNote.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedNote)))
            // Then: ожидаем, что статус ответа будет успешным (200 OK)
            .andExpect(status().isOk())
            // Then: проверяем, что заметка была обновлена с правильным title и content
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    void testDeleteNote() throws Exception {
        // Given: у нас есть существующая заметка с тестовыми данными

        // When: выполняем DELETE-запрос для удаления заметки
        mockMvc.perform(delete(BASE_URL + "/" + testNote.getId()))
            // Then: ожидаем, что статус ответа будет 204 (No Content), т.е. заметка была успешно удалена
            .andExpect(status().isNoContent());

        // Then: проверяем, что заметка была удалена из базы данных
        Optional<Note> deletedNote = noteRepository.findById(testNote.getId());
        assertThat(deletedNote).isEmpty();
    }

}
