package inote.controller;

import inote.entity.Note;
import inote.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @MockBean
    private NoteService noteService;  // Мокируем сервис для контроллера

    @Autowired
    private MockMvc mockMvc;  // Мокируем MVC для выполнения HTTP-запросов и проверки ответов

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Инициализация моков перед каждым тестом
    }

    // Test for GET /inote/notes
    @Test
    void testGetAllNotes() throws Exception {
        // Given: создаем тестовую заметку и список, который будет возвращен сервисом
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());
        List<Note> notes = Collections.singletonList(note);

        // When: имитируем вызов метода findAll() сервиса, который возвращает созданную заметку
        when(noteService.findAll()).thenReturn(notes);

        // Then: выполняем запрос GET и проверяем статус и содержимое ответа
        mockMvc.perform(get("/inote/notes"))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$[0].id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$[0].title").value("Test Note"))  // Проверяем название
            .andExpect(jsonPath("$[0].content").value("Test Content"));  // Проверяем содержимое
    }

    // Test for GET /inote/notes/1
    @Test
    void testGetNoteById() throws Exception {
        // Given: создаем заметку и возвращаем ее как Optional
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());

        // When: имитируем вызов findById() для получения заметки по ID
        when(noteService.findById(1L)).thenReturn(Optional.of(note));

        // Then: выполняем запрос GET для ID и проверяем статус и содержимое ответа
        mockMvc.perform(get("/inote/notes/1"))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$.id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$.title").value("Test Note"))  // Проверяем название
            .andExpect(jsonPath("$.content").value("Test Content"));  // Проверяем содержимое
    }

    // Test for GET /inote/notes/1 when note is not found
    @Test
    void testGetNoteById_NotFound() throws Exception {
        // Given: сервис возвращает Optional.empty() (не найдено)
        when(noteService.findById(1L)).thenReturn(Optional.empty());

        // When: выполняем запрос GET для несуществующей заметки
        // Then: ожидаем статус Not Found (404)
        mockMvc.perform(get("/inote/notes/1"))
            .andExpect(status().isNotFound());
    }

    // Test for GET /inote/notes/title/Test Note
    @Test
    void testGetNotesByTitle() throws Exception {
        // Given: создаем заметку и список, который будет возвращен при поиске по названию
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());
        List<Note> notes = Collections.singletonList(note);

        // When: имитируем вызов findByTitle() для поиска по названию
        when(noteService.findByTitle("Test Note")).thenReturn(notes);

        // Then: выполняем запрос GET по названию и проверяем статус и содержимое ответа
        mockMvc.perform(get("/inote/notes/title/Test Note"))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$[0].id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$[0].title").value("Test Note"))  // Проверяем название
            .andExpect(jsonPath("$[0].content").value("Test Content"));  // Проверяем содержимое
    }

    // Test for GET /inote/notes/title/Test Note when no notes found
    @Test
    void testGetNotesByTitle_NotFound() throws Exception {
        // Given: сервис возвращает пустой список (нет заметок с таким названием)
        when(noteService.findByTitle("Test Note")).thenReturn(Collections.emptyList());

        // When: выполняем запрос GET для названия, по которому нет заметок
        // Then: ожидаем статус Not Found (404)
        mockMvc.perform(get("/inote/notes/title/Test Note"))
            .andExpect(status().isNotFound());
    }

    // Test for GET /inote/notes/created-between?startDate=&endDate=
    @Test
    void testGetNotesByCreatedAtBetween() throws Exception {
        // Given: создаем заметку и список, который будет возвращен сервисом по диапазону дат
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());
        List<Note> notes = Collections.singletonList(note);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When: имитируем вызов findByCreatedAtBetween() для получения заметок по диапазону дат
        when(noteService.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(notes);

        // Then: выполняем запрос GET с параметрами дат и проверяем статус и содержимое ответа
        mockMvc.perform(get("/inote/notes/created-between")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$[0].id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$[0].title").value("Test Note"))  // Проверяем название
            .andExpect(jsonPath("$[0].content").value("Test Content"));  // Проверяем содержимое
    }

    // Test for GET /inote/notes/created-between when no notes found
    @Test
    void testGetNotesByCreatedAtBetween_NotFound() throws Exception {
        // Given: сервис возвращает пустой список (нет заметок в заданном диапазоне)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        when(noteService.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        // When: выполняем запрос GET с диапазоном дат, по которому нет заметок
        // Then: ожидаем статус Not Found (404)
        mockMvc.perform(get("/inote/notes/created-between")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
            .andExpect(status().isNotFound());
    }

    // Test for POST /inote/notes
    @Test
    void testAddNote() throws Exception {
        // Given: создаем заметку и сервис возвращает ее после сохранения
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());

        when(noteService.save(any(Note.class))).thenReturn(note);

        // When: выполняем запрос POST для добавления новой заметки
        // Then: проверяем статус и содержимое ответа
        mockMvc.perform(post("/inote/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Test Note\", \"content\": \"Test Content\"}"))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$.id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$.title").value("Test Note"))  // Проверяем название
            .andExpect(jsonPath("$.content").value("Test Content"));  // Проверяем содержимое
    }

    // Test for PUT /inote/notes/1
    @Test
    void testUpdateNote() throws Exception {
        // Given: создаем заметку и имитируем успешное обновление с новым содержанием
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Updated Note");
        note.setContent("Updated Content");
        note.setCreatedAt(LocalDateTime.now());

        when(noteService.update(anyLong(), any(Note.class))).thenReturn(Optional.of(note));

        // When: выполняем запрос PUT для обновления заметки по ID
        // Then: проверяем статус и содержимое ответа
        mockMvc.perform(put("/inote/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Updated Note\", \"content\": \"Updated Content\"}"))
            .andExpect(status().isOk())  // Статус должен быть OK
            .andExpect(jsonPath("$.id").value(1L))  // Проверяем ID
            .andExpect(jsonPath("$.title").value("Updated Note"))  // Проверяем название
            .andExpect(jsonPath("$.content").value("Updated Content"));  // Проверяем содержимое
    }

    // Test for PUT /inote/notes/1 when note is not found
    @Test
    void testUpdateNote_NotFound() throws Exception {
        // Given: сервис возвращает Optional.empty() (не найдено)
        when(noteService.update(anyLong(), any(Note.class))).thenReturn(Optional.empty());

        // When: выполняем запрос PUT для обновления несуществующей заметки
        // Then: ожидаем статус Not Found (404)
        mockMvc.perform(put("/inote/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Updated Note\", \"content\": \"Updated Content\"}"))
            .andExpect(status().isNotFound());
    }

    // Test for DELETE /inote/notes/1
    @Test
    void testDeleteNote() throws Exception {
        // Given: создаем заметку и возвращаем ее как Optional
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContent("Test Content");
        note.setCreatedAt(LocalDateTime.now());

        when(noteService.findById(1L)).thenReturn(Optional.of(note));

        // When: выполняем запрос DELETE для удаления заметки
        // Then: ожидаем статус No Content (204)
        mockMvc.perform(delete("/inote/notes/1"))
            .andExpect(status().isNoContent());
    }

    // Test for DELETE /inote/notes/1 when note is not found
    @Test
    void testDeleteNote_NotFound() throws Exception {
        // Given: сервис возвращает Optional.empty() (не найдено)
        when(noteService.findById(1L)).thenReturn(Optional.empty());

        // When: выполняем запрос DELETE для несуществующей заметки
        // Then: ожидаем статус Not Found (404)
        mockMvc.perform(delete("/inote/notes/1"))
            .andExpect(status().isNotFound());
    }
}