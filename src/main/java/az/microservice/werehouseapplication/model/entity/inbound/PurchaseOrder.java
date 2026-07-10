package az.microservice.werehouseapplication.model.entity.inbound;

import az.microservice.werehouseapplication.enums.PurchaseOrderStatus;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import az.microservice.werehouseapplication.model.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
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
    @JoinColumn(name = "vendor_id", nullable = false)
    private Partner vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, unique = true)
    private String orderNumber; // PO-2024-0001

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status; // draft, confirmed, partially_received, received, cancelled

    @Column(name = "expected_at")
    private LocalDateTime expectedAt; // gözlənilən gəliş tarixi

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = PurchaseOrderStatus.DRAFT;
    }
}
