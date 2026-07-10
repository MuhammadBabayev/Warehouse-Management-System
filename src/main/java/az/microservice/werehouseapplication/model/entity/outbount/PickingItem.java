package az.microservice.werehouseapplication.model.entity.outbount;

import az.microservice.werehouseapplication.enums.PickingItemStatus;
import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "picking_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_id", nullable = false)
    private Picking picking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; // hansı locationdan götürülməlidir

    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity; // götürülməli miqdar

    @Column(name = "picked_quantity", nullable = false)
    private Integer pickedQuantity = 0; // faktiki götürülən miqdar

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PickingItemStatus status; // pending, picked, shortage

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = PickingItemStatus.PENDING;
    }

}