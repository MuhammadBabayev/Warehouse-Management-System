package az.microservice.werehouseapplication.model.entity.outbount;

import az.microservice.werehouseapplication.enums.PickingStatus;
import az.microservice.werehouseapplication.model.entity.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pickings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Picking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo; // hansı anbardara tapşırıldı

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PickingStatus status; // pending, in_progress, completed

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = PickingStatus.PENDING;
    }


}