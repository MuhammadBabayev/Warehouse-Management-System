package az.microservice.werehouseapplication.model.entity.finance;

import az.microservice.werehouseapplication.enums.InvoiceStatus;
import az.microservice.werehouseapplication.enums.InvoiceType;
import az.microservice.werehouseapplication.model.entity.transfer.Transfer;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoice_transfer_type",
                        columnNames = {"transfer_id", "invoice_type"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy;

    @Column(nullable = false, unique = true)
    private String checkNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType invoiceType;

    @Column(nullable = false)
    private String fromWarehouseName;

    @Column(nullable = false)
    private String fromLocationName;

    @Column(nullable = false)
    private String toWarehouseName;

    @Column(nullable = false)
    private String toLocationName;

    @Column(nullable = false)
    private Integer totalItems;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalValue;    // sum of (qty * purchasePrice) for all items

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
        status = InvoiceStatus.DRAFT;
        createdAt = LocalDateTime.now();
    }
}
