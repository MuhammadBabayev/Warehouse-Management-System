package az.microservice.werehouseapplication.model.entity.outbount;

import az.microservice.werehouseapplication.enums.ShipmentStatus;
import az.microservice.werehouseapplication.model.entity.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver; // təyin edilmiş sürücü

    @Column(nullable = false, unique = true)
    private String trackingNumber; // izləmə nömrəsi

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // preparing, shipped, delivered, returned

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ShipmentStatus.PREPARING;
    }

}