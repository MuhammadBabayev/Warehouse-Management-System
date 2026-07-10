package az.microservice.werehouseapplication.model.entity.inbound;

import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_receipt_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_receipt_id", nullable = false)
    private InboundReceipt inboundReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; // hansı yerə yerləşdirildi

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity; // gözlənilən miqdar

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity; // faktiki gələn miqdar

    @Column(name = "rejected_quantity", nullable = false)
    private Integer rejectedQuantity = 0; // zədəli / qəbul edilməyən

    @Column
    private String notes; // zədəli olduqda səbəb

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
