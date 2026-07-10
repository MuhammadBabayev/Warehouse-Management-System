package az.microservice.werehouseapplication.model.entity.outbount;

import az.microservice.werehouseapplication.enums.SalesOrderStatus;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, unique = true)
    private String orderNumber; // SO-2024-0001

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SalesOrderStatus status; // draft, confirmed, picking, shipped, delivered, cancelled

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "expected_delivery_at")
    private LocalDateTime expectedDeliveryAt;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = SalesOrderStatus.DRAFT;
    }
}