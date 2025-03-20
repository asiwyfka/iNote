package inote.service.impl;

import inote.entity.Note;
import inote.exception.NotFoundException;
import inote.repository.NoteRepository;
import inote.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link NoteService}.
 * Сервис для работы с заметками.
 *
 * @author Avdeyev Viktor
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    /**
     * Получить все заметки.
     * Используется кэширование для хранения списка заметок.
     */
    @Override
    @Cacheable(value = "notes", key = "'allNotes'")
    public List<Note> findAll() {
        log.info("Запрос на получение всех заметок");
        List<Note> notes = noteRepository.findAll();
        log.info("Найдено {} заметок", notes.size());
        return notes;
    }

    /**
     * Найти заметку по ID.
     * Кэшируется результат для быстрого доступа по ID.
     */
    @Override
    @Cacheable(value = "notes", key = "#noteId")
    public Optional<Note> findById(Long noteId) {
        log.info("Поиск заметки по ID: {}", noteId);
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isEmpty()) {
            log.warn("Заметка с ID {} не найдена", noteId);
            throw new NotFoundException("Заметка с ID " + noteId + " не найдена");
        }
        log.info("Заметка найдена: {}", note.get());
        return note;
    }

    /**
     * Поиск заметок по заголовку.
     * Кэшируется список заметок по заголовку.
     */
    @Override
    @Cacheable(value = "notes", key = "#title")
    public List<Note> findByTitle(String title) {
        log.info("Поиск заметок с заголовком: '{}'", title);
        List<Note> notes = noteRepository.findByTitle(title);
        if (notes.isEmpty()) {
            log.warn("Заметки с заголовком '{}' не найдены", title);
            throw new NotFoundException("Заметки с заголовком '" + title + "' не найдены");
        }
        log.info("Найдено {} заметок с заголовком '{}'", notes.size(), title);
        return notes;
    }

    /**
     * Поиск заметок, созданных в указанный период.
     */
    @Override
    public List<Note> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Поиск заметок, созданных с {} по {}", startDate, endDate);
        List<Note> notes = noteRepository.findByCreatedAtBetween(startDate, endDate);
        if (notes.isEmpty()) {
            log.warn("Заметки не найдены в указанный период");
            throw new NotFoundException("Заметки не найдены в указанный период");
        }
        log.info("Найдено {} заметок в указанный период", notes.size());
        return notes;
    }

    /**
     * Сохранение или обновление заметки.
     * При сохранении заметки удаляется кэш для этой заметки.
     */
    @Override
    @CacheEvict(value = "notes", key = "#note.id")
    public Note save(Note note) {
        log.info("Сохранение заметки: {}", note);
        Note savedNote = noteRepository.save(note);
        log.info("Заметка сохранена: {}", savedNote);
        return savedNote;
    }

    /**
     * Обновление заметки по ID.
     * Кэш обновляется только для конкретной заметки.
     */
    @Override
    @CachePut(value = "notes", key = "#noteId")
    public Optional<Note> update(Long noteId, Note updatedNote) {
        log.info("Обновление заметки с ID: {}", noteId);

        Optional<Note> existingNote = noteRepository.findById(noteId);
        if (existingNote.isEmpty()) {
            log.warn("Заметка с ID {} не найдена для обновления", noteId);
            throw new NotFoundException("Заметка с ID " + noteId + " не найдена");
        }

        updatedNote.setId(noteId);
        Note savedNote = noteRepository.save(updatedNote);
        log.info("Заметка обновлена: {}", savedNote);
        return Optional.of(savedNote);
    }

    /**
     * Удалить заметку по ID.
     * Удаляется кэш для заметки, если она была удалена.
     */
    @Override
    @CacheEvict(value = "notes", key = "#noteId")
    public void deleteById(Long noteId) {
        log.info("Удаление заметки с ID: {}", noteId);
        if (noteRepository.findById(noteId).isEmpty()) {
            log.warn("Ошибка: заметка с ID {} не найдена, удаление невозможно", noteId);
            throw new NotFoundException("Заметка с ID " + noteId + " не найдена");
        }
        noteRepository.deleteById(noteId);
        log.info("Заметка с ID {} успешно удалена", noteId);
    }
}
