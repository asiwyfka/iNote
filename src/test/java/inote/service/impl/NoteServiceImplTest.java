package inote.service.impl;

import inote.entity.Note;
import inote.exception.NotFoundException;
import inote.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;  // Мокируем репозиторий для работы с данными

    @InjectMocks
    private NoteServiceImpl noteServiceImpl;  // Внедряем сервис, который тестируем

    private Note testNote;  // Тестовая заметка для использования в тестах

    @BeforeEach
    void setUp() {
        // Given: создаем заметку для использования в тестах
        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Title");
        testNote.setContent("Test Content");
        testNote.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenNoteDoesNotExist() {
        // Given: Мокируем репозиторий для возврата пустого Optional, что означает, что заметки не существует
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.empty());

        // When: вызываем метод поиска по ID
        // Then: ожидаем, что будет выброшено исключение NotFoundException, так как заметка не найдена
        assertThrows(NotFoundException.class, () -> noteServiceImpl.findById(1L));
    }

    @Test
    void findById_ShouldReturnNote_WhenNoteExists() {
        // Given: Мокируем репозиторий, чтобы он возвращал существующую заметку по ID
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.of(testNote));

        // When: вызываем метод для поиска заметки по ID
        Optional<Note> result = noteServiceImpl.findById(1L);

        // Then: проверяем, что результат не пустой и что данные заметки соответствуют ожидаемым
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testNote.getId());
        assertThat(result.get().getTitle()).isEqualTo(testNote.getTitle());
    }

    @Test
    void save_ShouldSaveAndReturnNote() {
        // Given: Мокируем репозиторий, чтобы он сохранял и возвращал новую заметку
        BDDMockito.given(noteRepository.save(ArgumentMatchers.any(Note.class))).willReturn(testNote);

        // When: сохраняем новую заметку с помощью метода save
        Note savedNote = noteServiceImpl.save(new Note());

        // Then: проверяем, что сохраненная заметка не null и что ее данные правильные
        assertThat(savedNote).isNotNull();
        assertThat(savedNote.getTitle()).isEqualTo("Test Title");
    }

    @Test
    void deleteById_ShouldThrowNotFoundException_WhenNoteDoesNotExist() {
        // Given: Мокируем репозиторий для возврата пустого Optional, что означает, что заметки нет в базе данных
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.empty());

        // When: вызываем метод удаления заметки
        // Then: ожидаем, что будет выброшено исключение NotFoundException, так как заметка не существует
        assertThrows(NotFoundException.class, () -> noteServiceImpl.deleteById(1L));
    }

    @Test
    void deleteById_ShouldDeleteNote_WhenNoteExists() {
        // Given: Мокируем репозиторий для возврата заметки по ID
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.of(testNote));

        // When: вызываем метод удаления заметки
        noteServiceImpl.deleteById(1L);

        // Then: проверяем, что метод deleteById был вызван один раз
        BDDMockito.verify(noteRepository, BDDMockito.times(1)).deleteById(1L);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenNoteDoesNotExist() {
        // Given: Мокируем репозиторий на возврат пустого Optional, что означает, что заметки нет
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.empty());

        // When: вызываем метод обновления заметки
        // Then: ожидаем, что будет выброшено исключение NotFoundException, так как заметка не найдена
        assertThrows(NotFoundException.class, () -> noteServiceImpl.update(1L, testNote));
    }

    @Test
    void update_ShouldReturnUpdatedNote_WhenNoteExists() {
        // Given: создаем обновленную заметку с новыми значениями
        Note updatedNote = new Note();
        updatedNote.setTitle("Updated Title");
        updatedNote.setContent("Updated Content");

        // Мокируем репозиторий на возвращение заметки по ID и сохранение обновленной заметки
        BDDMockito.given(noteRepository.findById(1L)).willReturn(Optional.of(testNote));
        BDDMockito.given(noteRepository.save(ArgumentMatchers.any(Note.class))).willReturn(updatedNote);

        // When: вызываем метод обновления заметки
        Optional<Note> result = noteServiceImpl.update(1L, updatedNote);

        // Then: проверяем, что обновленная заметка имеет правильные данные
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getContent()).isEqualTo("Updated Content");
    }

    @Test
    void findAll_ShouldReturnAllNotes() {
        // Given: Мокируем репозиторий для возврата списка заметок
        List<Note> notes = List.of(testNote);
        BDDMockito.given(noteRepository.findAll()).willReturn(notes);

        // When: вызываем метод для получения всех заметок
        List<Note> result = noteServiceImpl.findAll();

        // Then: проверяем, что список не пустой и содержит одну заметку
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
    }
}
