package az.microservice.werehouseapplication.model.entity.inbound;

import az.microservice.werehouseapplication.enums.InboundReceiptStatus;
import az.microservice.werehouseapplication.model.entity.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", nullable = false)
    private User receivedBy; // qəbul edən anbardar

    @Column(nullable = false, unique = true)
    private String receiptNumber; // REC-2024-0001

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InboundReceiptStatus status; // pending, completed, rejected

    @Column
    private String notes;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt; // faktiki qəbul tarixi

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = InboundReceiptStatus.PENDING;
        receivedAt = LocalDateTime.now();
    }

}




