package az.microservice.werehouseapplication.model.entity;

import az.microservice.werehouseapplication.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;      // "TRANSFER_CREATED"

    @Column(columnDefinition = "TEXT")
    private String payload;        // JSON of transfer data

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;   // PENDING, PROCESSED, FAILED

    private int retryCount;        // how many times tried

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = OutboxStatus.PENDING;
    }
}
