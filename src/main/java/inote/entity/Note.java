package inote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая заметку пользователя.
 * Содержит информацию о названии, содержимом, авторе заметки и времени создания/обновления.
 *
 * @author Avdeyev Viktor
 */
@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note implements Serializable {

    /**
     * Id заметки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Название заметки.
     * Обязательное поле с максимальной длиной 255 символов.
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Содержимое заметки.
     * Обязательное поле, может содержать текст любого размера.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Автор заметки (пользователь).
     * Ссылается на пользователя, который создал заметку.
     * Это внешний ключ на таблицу пользователей.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Время создания заметки.
     * Устанавливается автоматически в момент создания записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Время последнего обновления заметки.
     * Обновляется автоматически при изменении заметки.
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
