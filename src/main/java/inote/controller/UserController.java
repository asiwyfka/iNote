package inote.controller;

import inote.entity.User;
import inote.entity.enums.Role;
import inote.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления пользователями.
 *
 * @author Avdeyev Viktor
 */
@Tag(name = "Контроллер для управления пользователями")
@RestController
@Slf4j
@RequestMapping("/inote/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("getAllUsers - start");
        long startTime = System.currentTimeMillis();
        List<User> users = userService.findAll();
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Duration = {}", duration);
        log.info("getAllUsers - end, usersCount = {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получение пользователя по ID")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        log.info("getUserById - start, userId = {}", userId);
        Optional<User> user = userService.findById(userId);

        if (user.isEmpty()) {
            log.warn("getUserById - пользователь с ID {} не найден", userId);
            return ResponseEntity.notFound().build();
        }

        log.info("getUserById - end, user = {}", user.get());
        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Поиск пользователей по имени")
    @GetMapping("/search/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        log.info("getUsersByUsername - start, username = {}", username);
        List<User> users = userService.findByUsername(username);

        if (users.isEmpty()) {
            log.warn("getUsersByUsername - пользователи с именем '{}' не найдены", username);
            return ResponseEntity.notFound().build();
        }

        log.info("getUsersByUsername - end, usersCount = {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Поиск пользователя по email")
    @GetMapping("/search/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("getUserByEmail - start, email = {}", email);
        Optional<User> user = userService.findByEmail(email);

        if (user.isEmpty()) {
            log.warn("getUserByEmail - пользователь с email '{}' не найден", email);
            return ResponseEntity.notFound().build();
        }

        log.info("getUserByEmail - end, userId = {}", user.get().getId());
        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Поиск пользователей по роли")
    @GetMapping("/search/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        log.info("getUsersByRole - start, role = {}", role);

        Role userRole;
        try {
            userRole = Role.valueOf(role.toUpperCase()); // Преобразуем строку в Enum
        } catch (IllegalArgumentException e) {
            log.warn("getUsersByRole - некорректная роль: {}", role);
            return ResponseEntity.badRequest().build();
        }

        List<User> users = userService.findByRole(userRole);

        if (users.isEmpty()) {
            log.warn("getUsersByRole - пользователи с ролью '{}' не найдены", role);
            return ResponseEntity.notFound().build();
        }

        log.info("getUsersByRole - end, usersCount = {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Поиск пользователей, зарегистрированных после определенной даты")
    @GetMapping("/search/createdAfter/{date}")
    public ResponseEntity<List<User>> getUsersByCreatedAtAfter(@PathVariable String date) {
        log.info("getUsersByCreatedAtAfter - start, date = {}", date);

        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(date);
        } catch (Exception e) {
            log.warn("getUsersByCreatedAtAfter - некорректный формат даты: {}", date);
            return ResponseEntity.badRequest().build();
        }

        List<User> users = userService.findByCreatedAtAfter(parsedDate);

        if (users.isEmpty()) {
            log.warn("getUsersByCreatedAtAfter - пользователи, зарегистрированные после '{}', не найдены", date);
            return ResponseEntity.notFound().build();
        }

        log.info("getUsersByCreatedAtAfter - end, usersCount = {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Добавление нового пользователя")
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        log.info("addUser - start, user = {}", user);
        User savedUser = userService.save(user);
        log.info("addUser - end, savedUserId = {}", savedUser.getId());
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "Обновление данных пользователя")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        log.info("updateUser - start, userId = {}, user = {}", userId, user);
        Optional<User> updatedUser = userService.update(userId, user);

        if (updatedUser.isEmpty()) {
            log.warn("updateUser - пользователь с ID {} не найден", userId);
            return ResponseEntity.notFound().build();
        }

        log.info("updateUser - end, updatedUserId = {}", updatedUser.get().getId());
        return ResponseEntity.ok(updatedUser.get());
    }

    @Operation(summary = "Удаление пользователя по ID")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("deleteUser - start, userId = {}", userId);

        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            log.warn("deleteUser - пользователь с ID {} не найден", userId);
            return ResponseEntity.notFound().build();
        }

        userService.deleteById(userId);
        log.info("deleteUser - end, userId = {}", userId);
        return ResponseEntity.noContent().build();
    }
}