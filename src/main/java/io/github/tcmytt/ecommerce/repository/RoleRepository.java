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
}
