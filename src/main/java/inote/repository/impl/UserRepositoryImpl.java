package inote.repository.impl;

import inote.entity.User;
import inote.entity.enums.Role;
import inote.repository.UserRepository;
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
 * Реализация {@link UserRepository}.
 *
 * Использует {@link EntityManager} для взаимодействия с базой данных.
 *
 * @author Avdeyev Viktor
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("Запрос всех пользователей из базы данных");
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        log.info("Поиск пользователя по ID: {}", userId);
        User user = entityManager.find(User.class, userId);
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByUsername(String username) {
        log.info("Поиск пользователей по имени: {}", username);
        return entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.info("Поиск пользователя по email: {}", email);
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .getResultStream()
            .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        log.info("Поиск пользователей с ролью: {}", role);
        return entityManager.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
            .setParameter("role", role)
            .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByCreatedAtAfter(LocalDateTime date) {
        log.info("Поиск пользователей, зарегистрированных после: {}", date);
        return entityManager.createQuery("SELECT u FROM User u WHERE u.createdAt > :date", User.class)
            .setParameter("date", date)
            .getResultList();
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Сохранение пользователя: {}", user);
        if (user.getId() == null) {
            entityManager.persist(user);
            log.info("Новый пользователь сохранен: {}", user);
            return user;
        } else {
            User updatedUser = entityManager.merge(user);
            log.info("Пользователь обновлен: {}", updatedUser);
            return updatedUser;
        }
    }

    @Override
    @Transactional
    public Optional<User> update(Long userId, User updatedUser) {
        log.info("Обновление пользователя с ID: {}", userId);
        Optional<User> optionalUser = findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с ID {} не найден для обновления", userId);
            return Optional.empty();
        }

        User user = optionalUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setPassword(updatedUser.getPassword());

        user = entityManager.merge(user);
        log.info("Пользователь обновлен: {}", user);
        return Optional.of(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        Optional<User> user = findById(userId);
        user.ifPresentOrElse(
            entityManager::remove,
            () -> log.warn("Пользователь с ID {} не найден для удаления", userId)
        );
    }
}
