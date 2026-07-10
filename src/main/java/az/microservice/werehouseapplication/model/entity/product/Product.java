package az.microservice.werehouseapplication.model.entity.product;


import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WarehouseProductBarcode> barcodes;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String unit; // ədəd, kq, litr...

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 0; // minimum stok səviyyəsi

    @Column
    private Integer productCount = 0;

    @Column(columnDefinition = "TEXT",name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ItemStatus.ACTIVE;
    }

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    public String getPrimaryBarcode() {
        if (barcodes == null) return null;
        return barcodes.stream()
                .filter(WarehouseProductBarcode::isPrimary)
                .map(WarehouseProductBarcode::getBarcode)
                .findFirst()
                .orElse(null);
    }

}