package io.github.tcmytt.ecommerce.controller;

import java.util.List;

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

import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<User>> fetchAllWithPaginationAndSorting(
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
        return ResponseEntity.ok().body(this.userService.fetchAllWithPaginationAndSorting(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> fetchById(@PathVariable("id") long id) throws Exception {
        User u = this.userService.fetchById(id);
        if (u == null) {
            throw new Exception("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(u);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User u) throws Exception {
        // check email
        if (this.userService.isEmailExist(u.getEmail())) {
            throw new Exception("User với email = " + u.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(u));
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User u) throws Exception {
        if (this.userService.fetchById(u.getId()) == null) {
            throw new Exception("User với id = " + u.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(name = "keyword", defaultValue = "email") String keyword) {
        List<User> response = userService.searchUsers(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
