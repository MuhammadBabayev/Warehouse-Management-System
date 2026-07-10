package az.microservice.werehouseapplication.config;

import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.model.entity.users.*;
import az.microservice.werehouseapplication.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitialiazer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissonRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initializePermissions();
        initializeRoles();
        initializeSuperAdmin();
    }

    // region Permissions

    private void initializePermissions() {
        getAllPermissionNames().forEach(name ->
                permissionRepository.findByName(name)
                        .orElseGet(() -> permissionRepository.save(
                                Permissions.builder()
                                        .name(name)
                                        .description(name)
                                        .build()
                        ))
        );
    }

    private List<String> getAllPermissionNames() {
        return List.of(
                // Organization
                "organization.create",
                "organization.view",
                "organization.update",
                "organization.delete",
                // User
                "user.create",
                "user.view",
                "user.update",
                "user.delete",
                // Role
                "role.create",
                "role.view",
                "role.update",
                "role.delete",
                // Warehouse
                "warehouse.create",
                "warehouse.view",
                "warehouse.update",
                "warehouse.delete",
                // Inventory
                "inventory.create",
                "inventory.view",
                "inventory.update",
                "inventory.delete",
                // Inbound
                "inbound.create",
                "inbound.view",
                "inbound.update",
                "inbound.delete",
                // Outbound
                "outbound.create",
                "outbound.view",
                "outbound.update",
                "outbound.delete",
                // Orders
                "orders.create",
                "orders.view",
                "orders.update",
                "orders.delete",
                // Transfer
                "transfer.create",
                "transfer.view",
                "transfer.update",
                "transfer.delete",
                // Invoice
                "invoice.create",
                "invoice.view",
                "invoice.update",
                "invoice.delete",
                // Product
                "product.view",
                "product.create",
                "product.update",
                "product.delete",
                //vendor
                "vendor.view",
                "vendor.create",
                "vendor.update",
                "vendor.delete"
                // Location

        );
    }

    private void initializeRoles() {
        getRolePermissionMap().forEach(this::createRole);
    }

    private Map<String, List<String>> getRolePermissionMap() {
        return Map.of(
                "SUPER_ADMIN", List.of(
                        "organization.create", "organization.view", "organization.update", "organization.delete",
                        "user.create", "user.view", "user.update", "user.delete",
                        "role.create", "role.view", "role.update", "role.delete",
                        "warehouse.create", "warehouse.view", "warehouse.update", "warehouse.delete",
                        "inventory.create", "inventory.view", "inventory.update", "inventory.delete",
                        "inbound.create", "inbound.view", "inbound.update", "inbound.delete",
                        "outbound.create", "outbound.view", "outbound.update", "outbound.delete",
                        "orders.create", "orders.view", "orders.update", "orders.delete",
                        "transfer.create", "transfer.view", "transfer.update", "transfer.delete",
                        "invoice.create", "invoice.view", "invoice.update", "invoice.delete",
                        "product.create", "product.view", "product.update", "product.delete",
                        "vendor.create", "vendor.view", "vendor.update", "vendor.delete"
                ),
                "ADMIN", List.of(
                        "organization.view", "organization.update",
                        "user.create", "user.view", "user.update", "user.delete",
                        "role.create", "role.view", "role.update",
                        "warehouse.create", "warehouse.view", "warehouse.update", "warehouse.delete",
                        "inventory.create", "inventory.view", "inventory.update", "inventory.delete",
                        "inbound.create", "inbound.view", "inbound.update", "inbound.delete",
                        "outbound.create", "outbound.view", "outbound.update", "outbound.delete",
                        "orders.create", "orders.view", "orders.update", "orders.delete",
                        "transfer.create", "transfer.view", "transfer.update", "transfer.delete",
                        "invoice.create", "invoice.view", "invoice.update", "invoice.delete",
                        "product.create", "product.view", "product.update", "product.delete",
                        "vendor.create", "vendor.view", "vendor.update", "vendor.delete"
                ),
                "WAREHOUSE_MANAGER", List.of(
                        "warehouse.view", "warehouse.update",
                        "inventory.create", "inventory.view", "inventory.update",
                        "inbound.create", "inbound.view", "inbound.update",
                        "outbound.create", "outbound.view", "outbound.update",
                        "transfer.create", "transfer.view", "transfer.update",
                        "orders.view",
                        "product.view",
                        "vendor.view"
                ),
                "ACCOUNTANT", List.of(
                        "invoice.create", "invoice.view", "invoice.update",
                        "orders.view",
                        "inventory.view",
                        "inbound.view",
                        "outbound.view",
                        "vendor.view",
                        "product.view"
                ),
                "DRIVER", List.of(
                        "outbound.view",
                        "orders.view",
                        "transfer.view",
                        "warehouse.view"
                )
        );
    }

    private void createRole(String roleName, List<String> permissionNames) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .build()
                ));

        permissionNames.forEach(permissionName -> {
            permissionRepository.findByName(permissionName).ifPresent(permission -> {
                if (!rolePermissionRepository.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                    rolePermissionRepository.save(
                            RolePermission.builder()
                                    .role(role)
                                    .permission(permission)
                                    .build()
                    );
                }
            });
        });
    }

    // endregion

    // region SuperAdmin

    private void initializeSuperAdmin() {
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = User.builder()
                    .username("superadmin")
                    .email("superadmin@warehouse.az")
                    .password(passwordEncoder.encode("Admin123!"))
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(superAdmin);

            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                    .orElseThrow(() -> new RuntimeException("SUPER_ADMIN rolu tapılmadı"));

            UserRole userRole = UserRole.builder()
                    .user(superAdmin)
                    .role(superAdminRole)
                    .assignedBy(superAdmin)
                    .build();
            userRoleRepository.save(userRole);
        }
    }

    // endregion
}