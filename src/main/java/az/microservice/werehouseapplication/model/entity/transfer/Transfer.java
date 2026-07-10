package az.microservice.werehouseapplication.model.entity.transfer;

import az.microservice.werehouseapplication.enums.TransferStatus;
import az.microservice.werehouseapplication.model.entity.partner.Partner;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfer")
@SequenceGenerator(
        name = "transfer_number_seq",
        sequenceName = "transfer_number_seq",
        initialValue = 1,
        allocationSize = 1
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Partner vendor;       // PartnerType must be VENDOR or BOTH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Partner customer;     // PartnerType must be CUSTOMER or BOTH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(nullable = false, unique = true)
    private String transferNumber; // TRF-2024-0001

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column
    private String notes;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = TransferStatus.PENDING;
    }
}
