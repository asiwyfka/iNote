package inote.service;

import inote.entity.User;
import inote.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с {@link User}.
 * <p>
 * Определяет основные операции для управления пользователями.
 * </p>
 *
 * @author Avdeyev Viktor
 */
public interface UserService {

    /**
     * Получение всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Поиск пользователя по ID.
     *
     * @param userId уникальный идентификатор пользователя
     * @return объект {@link User}, если найден
     */
    Optional<User> findById(Long userId);

    /**
     * Поиск пользователей по имени.
     *
     * @param username имя пользователя
     * @return список пользователей с данным именем
     */
    List<User> findByUsername(String username);

    /**
     * Поиск пользователя по email.
     *
     * @param email email пользователя
     * @return объект {@link User}, если найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователей по роли.
     *
     * @param role роль пользователя
     * @return список пользователей с данной ролью
     */
    List<User> findByRole(Role role);

    /**
     * Поиск пользователей, зарегистрированных после указанной даты.
     *
     * @param date дата регистрации
     * @return список пользователей
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Сохранение пользователя.
     *
     * @param user пользователь для сохранения
     * @return сохраненный или обновленный пользователь
     */
    User save(User user);

    /**
     * Обновление данных пользователя.
     *
     * @param userId ID пользователя для обновления
     * @param updatedUser объект с обновленными данными
     * @return обновленный пользователь
     */
    Optional<User> update(Long userId, User updatedUser);

    /**
     * Удаление пользователя по ID.
     *
     * @param userId уникальный идентификатор пользователя
     */
    void deleteById(Long userId);
}
