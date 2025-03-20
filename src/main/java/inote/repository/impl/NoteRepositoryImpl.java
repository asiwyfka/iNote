package inote.repository.impl;

import inote.entity.Note;
import inote.entity.User;
import inote.repository.NoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link NoteRepository}.
 * <p>
 * Использует {@link EntityManager} для взаимодействия с базой данных.
 *
 * @author Avdeyev Viktor
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class NoteRepositoryImpl implements NoteRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAll() {
        log.info("Запрос всех заметок из базы данных");
        List<Note> notes = entityManager.createQuery("SELECT n FROM Note n", Note.class).getResultList();
        log.info("Найдено {} заметок", notes.size());
        return notes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByUser(User user) {
        log.info("Поиск заметок пользователя: {}", user.getUsername());
        List<Note> notes = entityManager.createQuery("SELECT n FROM Note n WHERE n.user = :user", Note.class)
            .setParameter("user", user)
            .getResultList();
        log.info("Найдено {} заметок для пользователя {}", notes.size(), user.getUsername());
        return notes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findByTitle(String title) {
        log.info("Поиск заметок по заголовку: {}", title);
        List<Note> notes = entityManager.createQuery("SELECT n FROM Note n WHERE n.title = :title", Note.class)
            .setParameter("title", title)
            .getResultList();
        log.info("Найдено {} заметок с заголовком '{}'", notes.size(), title);
        return notes;
    }

    @Override
    @Transactional
    public Note save(Note note) {
        log.info("Сохранение заметки: {}", note);
        if (note.getId() == null) {
            entityManager.persist(note);
            log.info("Новая заметка сохранена: {}", note);
            return note;
        } else {
            Note updatedNote = entityManager.merge(note);
            log.info("Заметка обновлена: {}", updatedNote);
            return updatedNote;
        }
    }

    @Override
    @Transactional
    public Optional<Note> update(Long id, Note updatedNote) {
        log.info("Обновление заметки с ID: {}", id);
        Optional<Note> optionalNote = findById(id);
        if (optionalNote.isEmpty()) {
            log.warn("Заметка с ID {} не найдена для обновления", id);
            return Optional.empty();
        }
        Note note = optionalNote.get();
        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());
        note.setUpdatedAt(updatedNote.getUpdatedAt() != null ? updatedNote.getUpdatedAt() : note.getUpdatedAt());

        note = entityManager.merge(note);
        log.info("Заметка обновлена: {}", note);
        return Optional.of(note);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Удаление заметки с ID: {}", id);
        Optional<Note> note = findById(id);
        if (note.isPresent()) {
            entityManager.remove(note.get());
            log.info("Заметка удалена: {}", note.get());
        } else {
            log.warn("Заметка с ID {} не найдена для удаления", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findById(Long id) {
        log.info("Поиск заметки по ID: {}", id);
        Note note = entityManager.find(Note.class, id);
        if (note != null) {
            log.info("Заметка с ID {} найдена", id);
        } else {
            log.warn("Заметка с ID {} не найдена", id);
        }
        return Optional.ofNullable(note);
    }
}
