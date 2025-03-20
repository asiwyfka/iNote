package inote.service.impl;

import inote.entity.User;
import inote.entity.enums.Role;
import inote.exception.NotFoundException;
import inote.repository.UserRepository;
import inote.service.UserService;
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
 * Реализация {@link UserService}.
 *
 * @author Avdeyev Viktor
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "'allUsers'")
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        List<User> users = userRepository.findAll();
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public Optional<User> findById(Long userId) {
        log.info("Поиск пользователя по ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        log.info("Пользователь найден: {}", user.get());
        return user;
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public List<User> findByUsername(String username) {
        log.info("Поиск пользователей по имени: {}", username);
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            log.warn("Пользователи с именем '{}' не найдены", username);
            throw new NotFoundException("Пользователи с именем '" + username + "' не найдены");
        }
        log.info("Найдено {} пользователей с именем '{}'", users.size(), username);
        return users;
    }
    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        log.info("Поиск пользователя по email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("Пользователь с email '{}' не найден", email);
            throw new NotFoundException("Пользователь с email '" + email + "' не найден");
        }
        log.info("Пользователь найден: {}", user.get());
        return user;
    }
    @Override
    @Cacheable(value = "users", key = "#role")
    public List<User> findByRole(Role role) {
        log.info("Поиск пользователей с ролью: {}", role);
        List<User> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            log.warn("Пользователи с ролью '{}' не найдены", role);
            throw new NotFoundException("Пользователи с ролью '" + role + "' не найдены");
        }
        log.info("Найдено {} пользователей с ролью '{}'", users.size(), role);
        return users;
    }

    @Override
    @Cacheable(value = "users", key = "#date")
    public List<User> findByCreatedAtAfter(LocalDateTime date) {
        log.info("Поиск пользователей, зарегистрированных после {}", date);
        List<User> users = userRepository.findByCreatedAtAfter(date);
        if (users.isEmpty()) {
            log.warn("Не найдено пользователей, зарегистрированных после {}", date);
            throw new NotFoundException("Пользователи, зарегистрированные после " + date + ", не найдены");
        }
        log.info("Найдено {} пользователей, зарегистрированных после {}", users.size(), date);
        return users;
    }

    @Override
    @CacheEvict(value = "users", key = "#user.id")
    public User save(User user) {
        log.info("Сохранение пользователя: {}", user);
        User savedUser = userRepository.save(user);
        log.info("Пользователь сохранен: {}", savedUser);
        return savedUser;
    }

    @Override
    @CachePut(value = "users", key = "#userId")
    public Optional<User> update(Long userId, User updatedUser) {
        log.info("Обновление пользователя с ID: {}", userId);

        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            log.warn("Пользователь с ID {} не найден для обновления", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        updatedUser.setId(userId);
        User savedUser = userRepository.save(updatedUser);
        log.info("Пользователь обновлен: {}", savedUser);
        return Optional.of(savedUser);
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void deleteById(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Ошибка: пользователь с ID {} не найден, удаление невозможно", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с ID {} успешно удален", userId);
    }
}
