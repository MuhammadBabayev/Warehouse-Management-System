package az.microservice.werehouseapplication.model.entity.warehouse;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.LocationStatus;
import az.microservice.werehouseapplication.model.entity.transfer.Inventory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "location")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @Column(nullable = false)
    private String code; // "A-01-Sol", "A-01-Sağ", "A-01-Üst"

    @OneToMany(mappedBy = "location", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    private Long maxCapacity;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationStatus status; // active, inactive, full

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status=LocationStatus.ACTIVE;
        itemStatus=ItemStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
