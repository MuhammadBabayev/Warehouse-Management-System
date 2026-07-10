package az.microservice.werehouseapplication.model.entity.transfer;

import az.microservice.werehouseapplication.model.entity.product.Product;
import az.microservice.werehouseapplication.model.entity.warehouse.Location;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;


    @Column
    private Integer availableQuantity = 0;

    @Column
    private Integer reservedQuantity = 0;

    @Column
    private Integer damagedQuantity = 0;

    @Column
    private Integer totalQuantity = 0;
}
