package io.github.tcmytt.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Permission;
import io.github.tcmytt.ecommerce.domain.Role;
import io.github.tcmytt.ecommerce.repository.PermissionRepository;
import io.github.tcmytt.ecommerce.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

    public Role fetchByName(String name) {
        return this.roleRepository.findByName(name);
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role r) {
        // Khởi tạo role truyền list id permission từ list id đó truyền vào list
        // permission

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.getActive());
        roleDB.setPermissions(r.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return this.roleRepository.save(roleDB);
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public List<Role> fetchAll() {
        return this.roleRepository.findAll();
    }

    public Page<Role> fetchByPaginationAndSorting(Pageable pageable) {
        return this.roleRepository.findAll(pageable);
    }

}
