package az.microservice.werehouseapplication.model.entity.users;

import az.microservice.werehouseapplication.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "inventory.view", "orders.create"

    private String description;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @PrePersist
    protected void onCreate() {
        this.status= ItemStatus.ACTIVE;
    }

}
