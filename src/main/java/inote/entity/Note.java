package inote.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая заметку.
 * Содержит информацию о названии, содержимом и времени создания/обновления.
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
     * Обязательное поле с максимальной длиной 50 символов.
     */
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * Содержимое заметки.
     * Обязательное поле, может содержать текст любого размера.
     */
    @Column(nullable = false)
    private String content;

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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // Устанавливаем значение createdAt
        }
        updatedAt = LocalDateTime.now(); // Также можно обновить updatedAt
    }
}
