package inote.entity;

import inote.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность с информацией о пользователях.
 * Пользователь может иметь несколько заметок.
 *
 * @author Avdeyev Viktor
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    /**
     * Id пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Логин пользователя.
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Username cannot be null")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    private String username;

    /**
     * Email пользователя.
     */
    @Column(nullable = false, unique = true, length = 100)
    @NotNull(message = "Email cannot be null")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    /**
     * Пароль пользователя.
     */
    @Column(nullable = false)
    @NotNull(message = "Password cannot be null")
    private String password;

    /**
     * Роль пользователя.
     * По умолчанию "USER".
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Время регистрации пользователя.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Время обновления пользователя.
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Время последнего входа пользователя.
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Список заметок, принадлежащих пользователю.
     * Связь "один ко многим" с сущностью Note.
     */
    @OneToMany(mappedBy = "user")
    private List<Note> notes;
}
