package inote.service.impl;

import inote.entity.Note;
import inote.entity.User;
import inote.exception.NotFoundException;
import inote.repository.NoteRepository;
import inote.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link NoteService}.
 *
 * @author Avdeyev Viktor
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    @Cacheable(value = "notes", key = "'allNotes'")
    public List<Note> findAll() {
        log.info("Запрос на получение всех заметок");
        List<Note> notes = noteRepository.findAll();
        log.info("Найдено {} заметок", notes.size());
        return notes;
    }

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

    @Override
    @Cacheable(value = "notes", key = "#user.id")
    public List<Note> findByUser(User user) {
        log.info("Поиск заметок для пользователя: {}", user.getId());
        List<Note> notes = noteRepository.findByUser(user);
        if (notes.isEmpty()) {
            log.warn("Заметки для пользователя с ID {} не найдены", user.getId());
            throw new NotFoundException("Заметки для пользователя с ID " + user.getId() + " не найдены");
        }
        log.info("Найдено {} заметок для пользователя с ID {}", notes.size(), user.getId());
        return notes;
    }

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


    @Override
    @CacheEvict(value = "notes", key = "#note.id")
    public Note save(Note note) {
        log.info("Сохранение заметки: {}", note);
        Note savedNote = noteRepository.save(note);
        log.info("Заметка сохранена: {}", savedNote);
        return savedNote;
    }

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
