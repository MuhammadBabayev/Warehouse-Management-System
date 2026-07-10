package az.microservice.werehouseapplication.model.entity.transfer;

import az.microservice.werehouseapplication.enums.MovementType;
import az.microservice.werehouseapplication.enums.ReferenceType;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy; // kim etdi

    @Column(nullable = false)
    private Integer quantity; // neçə dəyişdi (mənfi ola bilər)

    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore; // dəyişməzdən əvvəl

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter; // dəyişdikdən sonra

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type; // inbound, outbound, transfer, adjustment

    @Column(name = "reference_type")
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType; // purchase_order, sales_order, transfer, manual

    @Column(name = "reference_id")
    private Long referenceId;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}