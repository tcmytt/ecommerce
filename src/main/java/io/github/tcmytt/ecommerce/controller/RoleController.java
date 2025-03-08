package io.github.tcmytt.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.tcmytt.ecommerce.domain.Role;
import io.github.tcmytt.ecommerce.service.RoleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws Exception {
        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new Exception("Role với name = " + r.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws Exception {
        if (this.roleService.existByName(r.getName()) == false) {
            throw new Exception("Role với name = " + r.getName() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) throws Exception {
        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new Exception("Role với id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body("Xoá thành công");
    }

    // @GetMapping
    // public ResponseEntity<List<Role>> fetchAll() {
    // return ResponseEntity.ok().body(this.roleService.fetchAll());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<Role> fetchById(@PathVariable("id") long id) throws Exception {
        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new Exception("Role với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(role);
    }

    @GetMapping
    public Page<Role> fetchByPaginationAndSorting(
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        int page = 0;
        int size = 10;

        // Xác định tiêu chí sắp xếp
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Truy vấn dữ liệu
        return roleService.fetchByPaginationAndSorting(pageable);
    }
}
