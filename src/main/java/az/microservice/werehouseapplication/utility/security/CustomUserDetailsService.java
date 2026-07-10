package az.microservice.werehouseapplication.utility.security;

import az.microservice.werehouseapplication.enums.ItemStatus;
import az.microservice.werehouseapplication.enums.UserStatus;
import az.microservice.werehouseapplication.exception.NotFoundException;
import az.microservice.werehouseapplication.model.entity.users.User;
import az.microservice.werehouseapplication.model.entity.users.UserRole;
import az.microservice.werehouseapplication.repository.RolePermissonRepository;
import az.microservice.werehouseapplication.repository.UserRepository;
import az.microservice.werehouseapplication.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static az.microservice.werehouseapplication.exception.ExceptionMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissonRepository rolePermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getMessage()));

        if (user.getStatus() != UserStatus.ACTIVE || user.getItemStatus() != ItemStatus.ACTIVE) {
            throw new NotFoundException(USER_NOT_FOUND.getMessage());
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        List<UserRole> userRoles = userRoleRepository.findByUser(user);

        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName().toUpperCase()));

            rolePermissionRepository.findByRoleId(userRole.getRole().getId())
                    .stream()
                    .map(rp -> new SimpleGrantedAuthority(rp.getPermission().getName()))
                    .forEach(authorities::add);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }

}
