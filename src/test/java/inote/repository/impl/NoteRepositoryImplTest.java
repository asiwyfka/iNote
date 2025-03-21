package inote.repository.impl;

import inote.entity.Note;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NoteRepositoryImplTest {

    @InjectMocks
    private NoteRepositoryImpl noteRepository;  // Внедряем реализацию репозитория

    @Mock
    private EntityManager entityManager;  // Мокируем EntityManager для работы с базой данных

    @Mock
    private TypedQuery<Note> typedQuery; // Используем TypedQuery для выполнения запросов

    private Note testNote; // Тестовая заметка для использования в тестах

    @BeforeEach
    public void setUp() {
        // Given: создаем объект заметки для использования в тестах
        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Title");
        testNote.setContent("Test Content");
    }

    @Test
    public void testFindAll_ShouldReturnNotes_WhenNotesExist() {
        // Given: мокируем создание запроса с использованием EntityManager и результат
        when(entityManager.createQuery("SELECT n FROM Note n", Note.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(testNote));

        // When: вызываем метод репозитория
        List<Note> result = noteRepository.findAll();

        // Then: проверяем, что результат не пустой
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByTitle_ShouldReturnNotes_WhenNotesExist() {
        // Given: мокируем создание запроса с использованием EntityManager, устанавливаем параметры и результат
        when(entityManager.createQuery("SELECT n FROM Note n WHERE n.title = :title", Note.class)).thenReturn(
            typedQuery);
        when(typedQuery.setParameter("title", "Test Title")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(testNote));

        // When: вызываем метод репозитория для поиска по заголовку
        List<Note> result = noteRepository.findByTitle("Test Title");

        // Then: проверяем, что результат не пустой и что он содержит одну заметку с правильным заголовком
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());

        // Проверка, что методы были вызваны правильно
        verify(entityManager, times(1)).createQuery("SELECT n FROM Note n WHERE n.title = :title", Note.class);
        verify(typedQuery, times(1)).setParameter("title", "Test Title");
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void testFindByTitle_ShouldReturnEmptyList_WhenNoNotesExist() {
        // Given: мокируем создание запроса с использованием EntityManager, устанавливаем параметры и результат
        when(entityManager.createQuery("SELECT n FROM Note n WHERE n.title = :title", Note.class)).thenReturn(
            typedQuery);
        when(typedQuery.setParameter("title", "Nonexistent Title")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());  // Мокируем пустой список

        // When: вызываем метод репозитория для поиска по заголовку
        List<Note> result = noteRepository.findByTitle("Nonexistent Title");

        // Then: проверяем, что результат пустой
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Проверка, что методы были вызваны правильно
        verify(entityManager, times(1)).createQuery("SELECT n FROM Note n WHERE n.title = :title", Note.class);
        verify(typedQuery, times(1)).setParameter("title", "Nonexistent Title");
        verify(typedQuery, times(1)).getResultList();
    }

    @Test
    public void testSave_ShouldSaveNewNote_WhenNoteIsNew() {
        // Given: устанавливаем ID заметки как null, чтобы показать, что это новая заметка
        testNote.setId(null);

        // мокаем persist для возврата заметки с установленным ID
        doAnswer(invocation -> {
            Note savedNote = invocation.getArgument(0);
            savedNote.setId(1L);
            return null;
        }).when(entityManager).persist(any(Note.class));

        // When: вызываем метод save для сохранения новой заметки
        Note savedNote = noteRepository.save(testNote);

        // Then: проверяем, что заметка не null и ID был присвоен
        assertNotNull(savedNote);
        assertNotNull(savedNote.getId());  // ID не должен быть null после сохранения
        assertEquals(1L, savedNote.getId());  // ID должен быть равен 1L
        verify(entityManager, times(1)).persist(any(Note.class)); // Проверка, что метод persist был вызван
    }

    @Test
    public void testSave_ShouldUpdateNote_WhenNoteExists() {
        // Given: создаем заметку с ID 1L и мокаем вызов merge
        testNote.setId(1L);
        when(entityManager.merge(testNote)).thenReturn(testNote);

        // When: вызываем метод save для обновления существующей заметки
        Note updatedNote = noteRepository.save(testNote);

        // Then: проверяем, что обновленная заметка не пуста
        assertNotNull(updatedNote);
        assertEquals(testNote.getId(), updatedNote.getId());
        verify(entityManager, times(1)).merge(testNote); // Проверка, что был вызван метод merge
    }

    @Test
    public void testUpdate_ShouldReturnUpdatedNote_WhenNoteExists() {
        // Given: мокаем нахождение заметки и ее обновление
        testNote.setId(1L);
        when(entityManager.find(Note.class, 1L)).thenReturn(testNote);
        when(entityManager.merge(testNote)).thenReturn(testNote);

        // When: вызываем метод update для обновления заметки
        Optional<Note> updatedNote = noteRepository.update(1L, testNote);

        // Then: проверяем, что обновленная заметка существует и ID совпадает
        assertTrue(updatedNote.isPresent());
        assertEquals(testNote.getId(), updatedNote.get().getId());
        verify(entityManager, times(1)).find(Note.class, 1L);  // Проверка нахождения заметки
        verify(entityManager, times(1)).merge(testNote);  // Проверка, что merge был вызван
    }

    @Test
    public void testDelete_ShouldDeleteNote_WhenNoteExists() {
        // Given: мокаем нахождение заметки с ID 1L
        testNote.setId(1L);
        when(entityManager.find(Note.class, 1L)).thenReturn(testNote);

        // When: вызываем метод delete для удаления заметки
        noteRepository.deleteById(1L);

        // Then: проверяем, что метод remove был вызван
        verify(entityManager, times(1)).remove(testNote);
    }

    @Test
    public void testDelete_ShouldDoNothing_WhenNoteDoesNotExist() {
        // Given: мокаем, что заметка не найдена
        when(entityManager.find(Note.class, 1L)).thenReturn(null);

        // When: вызываем метод delete для удаления несуществующей заметки
        noteRepository.deleteById(1L);

        // Then: проверяем, что метод remove не был вызван
        verify(entityManager, times(0)).remove(any());
    }
}
