package inote.service;

import inote.entity.Note;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с {@link Note}.
 *
 * Определяет основные операции для управления заметками.
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
     * Поиск заметок по заголовку.
     *
     * @param title заголовок заметки
     * @return список заметок с указанным заголовком
     */
    List<Note> findByTitle(String title);

    /**
     * Поиск заметок, созданных в указанный период.
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список заметок, созданных в указанный период
     */
    List<Note> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Сохранение новой или обновление существующей заметки.
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
     * @return Optional с обновленной заметкой, если она была найдена
     */
    Optional<Note> update(Long noteId, Note updatedNote);

    /**
     * Удаление заметки по ID.
     *
     * @param noteId уникальный идентификатор заметки
     */
    void deleteById(Long noteId);
}
