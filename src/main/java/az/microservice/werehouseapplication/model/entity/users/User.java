package az.microservice.werehouseapplication.model.entity.users;


import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.model.entity.warehouse.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String phone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    @Enumerated(EnumType.STRING)
    ItemStatus itemStatus;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = UserStatus.ACTIVE;
        this.itemStatus= ItemStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

//    public String getUsername() {
//        return email;  // email ilə login
//    }

    public boolean isAccountNonExpired() {
        return true;
    }


    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    public String getPassword() {
        return password;
    }
}
