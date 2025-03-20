package inote.repository;

import inote.entity.Note;
import inote.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с {@link Note}.
 *
 * Интерфейс определяет методы для работы с сущностью заметок.
 *
 * @author Avdeyev Viktor
 */
@Repository
public interface NoteRepository {

    /**
     * Найти все заметки.
     *
     * @return список всех заметок.
     */
    List<Note> findAll();

    /**
     * Найти заметку по ID.
     *
     * @param id ID заметки.
     * @return Optional с объектом Note, если найден.
     */
    Optional<Note> findById(Long id);

    /**
     * Найти все заметки, принадлежащие пользователю.
     *
     * @param user пользователь.
     * @return список заметок, принадлежащих данному пользователю.
     */
    List<Note> findByUser(User user);

    /**
     * Найти все заметки по заголовку.
     *
     * @param title заголовок заметки.
     * @return список заметок с указанным заголовком.
     */
    List<Note> findByTitle(String title);

    /**
     * Сохранить или обновить заметку.
     *
     * @param note объект заметки.
     * @return сохраненная или обновленная заметка.
     */
    Note save(Note note);

    /**
     * Обновить заметку по ID.
     *
     * @param id ID заметки.
     * @param updatedNote объект заметки с новыми данными.
     * @return Optional с обновленной заметкой.
     */
    Optional<Note> update(Long id, Note updatedNote);

    /**
     * Удалить заметку по ID.
     *
     * @param id ID заметки.
     */
    void deleteById(Long id);
}
