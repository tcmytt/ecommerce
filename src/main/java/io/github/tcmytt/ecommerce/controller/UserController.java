package io.github.tcmytt.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.service.FileStorageService;
import io.github.tcmytt.ecommerce.service.UserService;
import io.github.tcmytt.ecommerce.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final SecurityUtil securityUtil;

    @Value("${file.user-avatar-dir}")
    private String userAvatarDir;

    public UserController(UserService userService,
            FileStorageService fileStorageService,
            SecurityUtil securityUtil) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.securityUtil = securityUtil;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
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

    @Operation(summary = "Get user by id", description = "Get a user by id")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @ApiResponse(responseCode = "400", description = "User with id does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<User> fetchById(@PathVariable("id") long id) throws Exception {
        User u = this.userService.fetchById(id);
        if (u == null) {
            throw new Exception("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(u);
    }

    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "User with email already exists")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User u) throws Exception {
        // check email
        if (this.userService.isEmailExist(u.getEmail())) {
            throw new Exception("User với email = " + u.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.handleCreateUser(u));
    }

    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "User with id does not exist")
    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User u) throws Exception {
        if (this.userService.fetchById(u.getId()) == null) {
            throw new Exception("User với id = " + u.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(u));
    }

    @Operation(summary = "Delete user", description = "Delete an existing user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "Search users", description = "Search users by keyword")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(name = "keyword", defaultValue = "email") String keyword) {
        List<User> response = userService.searchUsers(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Update user avatar", description = "Update user avatar")
    @ApiResponse(responseCode = "200", description = "User avatar updated successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<User> updateAvatar(
            @RequestPart("file") MultipartFile file) {

        // Lấy user hiện tại từ SecurityContext
        User currentUser = securityUtil.getCurrentUser();

        // Lưu ảnh mới và lấy đường dẫn
        String newAvatarPath = fileStorageService.storeAvatar(file);

        // Xóa ảnh cũ nếu có
        if (currentUser.getAvatar() != null) {
            fileStorageService.deleteFile(currentUser.getAvatar(), userAvatarDir);
        }

        // Cập nhật avatar mới
        currentUser.setAvatar(newAvatarPath);
        User updatedUser = userService.handleUpdateUser(currentUser);

        return ResponseEntity.ok(updatedUser);
    }
}
