package inote.repository.impl;

import inote.entity.Note;
import inote.repository.NoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link NoteRepository}.
 *
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
    public Optional<Note> findById(Long id) {
        log.info("Поиск заметки по ID: {}", id);
        Note note = entityManager.find(Note.class, id);
        return Optional.ofNullable(note);
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
    @Transactional(readOnly = true)
    public List<Note> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Поиск заметок, созданных между {} и {}", startDate, endDate);
        List<Note> notes =
            entityManager.createQuery("SELECT n FROM Note n WHERE n.createdAt BETWEEN :startDate AND :endDate",
                    Note.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
        log.info("Найдено {} заметок, созданных в указанный период", notes.size());
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
}
