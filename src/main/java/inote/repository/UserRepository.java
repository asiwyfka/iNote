package inote.repository;

import inote.entity.User;
import inote.entity.enums.Role;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с {@link User}.
 *
 * Интерфейс определяет методы для работы с сущностью пользователей.
 *
 * @author Avdeyev Viktor
 */
@Repository
public interface UserRepository {

    /**
     * Найти всех пользователей.
     *
     * @return список пользователей.
     */
    List<User> findAll();

    /**
     * Найти пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return Optional с объектом User, если найден.
     */
    Optional<User> findById(Long userId);

    /**
     * Найти пользователя по имени.
     *
     * @param username имя пользователя.
     * @return список пользователей с данным именем.
     */
    List<User> findByUsername(String username);

    /**
     * Найти пользователя по email.
     *
     * @param email email пользователя.
     * @return Optional с объектом User, если найден.
     */
    Optional<User> findByEmail(String email);

    /**
     * Найти пользователей по роли.
     *
     * @param role роль пользователя.
     * @return список пользователей с данной ролью.
     */
    List<User> findByRole(Role role);

    /**
     * Найти пользователей по времени регистрации после указанной даты.
     *
     * @param date дата регистрации.
     * @return список пользователей.
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Сохранить нового пользователя или обновить существующего.
     *
     * @param user объект пользователя.
     * @return сохраненный объект пользователя.
     */
    User save(User user);

    /**
     * Обновить данные пользователя по ID.
     *
     * @param userId ID пользователя.
     * @param updatedUser объект пользователя с новыми данными.
     * @return Optional с обновленным объектом пользователя.
     */
    Optional<User> update(Long userId, User updatedUser);

    /**
     * Удалить пользователя по ID.
     *
     * @param userId ID пользователя.
     */
    void deleteById(Long userId);
}
