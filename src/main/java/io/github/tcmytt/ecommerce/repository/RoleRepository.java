package io.github.tcmytt.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    boolean existsByName(String name);

    @NonNull
    List<Role> findAll();

    @NonNull
    Page<Role> findAll(@NonNull Pageable pageable);

    // Kiểm tra xem có tồn tại role ADMIN hay không
    default boolean isAdminRoleExists() {
        return existsByName("ADMIN");
    }

    // Kiểm tra xem có tồn tại role USER hay không
    default boolean isUserRoleExists() {
        return existsByName("USER");
    }

    // Kiểm tra xem một role có phải là ADMIN hay không
    default boolean isAdminRole(Role role) {
        return role != null && "ADMIN".equals(role.getName());
    }

    // Kiểm tra xem một role có phải là USER hay không
    default boolean isUserRole(Role role) {
        return role != null && "USER".equals(role.getName());
    }

}
