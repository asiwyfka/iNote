package inote.service;

import inote.entity.Note;
import inote.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с {@link Note}.
 * <p>
 * Определяет основные операции для управления заметками.
 * </p>
 *
 * @author Avdeyev Viktor
 */
public interface NoteService {

    /**
     * Получение всех заметок.
     *
     * @return список всех заметок
     */
    List<Note> findAll();

    /**
     * Поиск заметки по ID.
     *
     * @param noteId уникальный идентификатор заметки
     * @return объект {@link Note}, если найден
     */
    Optional<Note> findById(Long noteId);

    /**
     * Поиск заметок пользователя.
     *
     * @param user пользователь, которому принадлежат заметки
     * @return список заметок пользователя
     */
    List<Note> findByUser(User user);

    /**
     * Поиск заметок по заголовку.
     *
     * @param title заголовок заметки
     * @return список заметок с указанным заголовком
     */
    List<Note> findByTitle(String title);

    /**
     * Сохранение заметки.
     *
     * @param note заметка для сохранения
     * @return сохраненная или обновленная заметка
     */
    Note save(Note note);

    /**
     * Обновление заметки.
     *
     * @param noteId ID заметки для обновления
     * @param updatedNote объект с обновленными данными
     * @return обновленная заметка
     */
    Optional<Note> update(Long noteId, Note updatedNote);

    /**
     * Удаление заметки по ID.
     *
     * @param noteId уникальный идентификатор заметки
     */
    void deleteById(Long noteId);
}
