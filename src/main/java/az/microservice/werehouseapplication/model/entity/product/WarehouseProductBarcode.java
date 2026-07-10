package az.microservice.werehouseapplication.model.entity.product;

import az.microservice.werehouseapplication.enums.BarcodeType;
import az.microservice.werehouseapplication.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_product_barcodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseProductBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BarcodeType type;

    @Column(nullable = false)
    private Integer quantity = 1; // box tipindədirsə içində neçə məhsul var, individual üçün 1

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false; // əsas barkod

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ItemStatus.ACTIVE;
    }
}