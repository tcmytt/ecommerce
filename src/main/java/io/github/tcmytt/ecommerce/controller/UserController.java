package io.github.tcmytt.ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import io.github.tcmytt.ecommerce.domain.request.ReqResetPasswordDTO;
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

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final SecurityUtil securityUtil;

    @Value("${file.user-avatar-dir}")
    private String userAvatarDir;

    public UserController(UserService userService,
            FileStorageService fileStorageService,
            SecurityUtil securityUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Get users", description = "Returns a list of users with pagination, sorting, or search by keyword")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<User>> fetchUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        // Xác định tiêu chí sắp xếp
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Kiểm tra xem có tìm kiếm hay không
        if (keyword != null && !keyword.isEmpty()) {
            // Tìm kiếm người dùng với phân trang
            return ResponseEntity.ok().body(userService.searchUsersWithPagination(keyword, pageable));
        } else {
            // Lấy danh sách người dùng với phân trang và sắp xếp
            return ResponseEntity.ok().body(userService.fetchAllWithPaginationAndSorting(pageable));
        }
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

        // Lấy thông tin người dùng hiện tại
        User currentUser = this.userService.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Không tìm thấy thông tin người dùng hiện tại.");
        }

        // Kiểm tra quyền truy cập
        boolean isAdmin = this.userService.isCurrentUserAdmin();
        if (!isAdmin && u != null && !u.getId().equals(currentUser.getId())) {
            throw new Exception("Bạn không có quyền cập nhật thông tin của người khác.");
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

    @Operation(summary = "Reset password", description = "Reset password")
    @ApiResponse(responseCode = "200", description = "Reset password successfully")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ReqResetPasswordDTO reqResetPasswordDTO) {
        // Lấy thông tin người dùng hiện tại
        User user = this.userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
        }

        // Kiểm tra mật khẩu cũ
        if (!this.passwordEncoder.matches(reqResetPasswordDTO.getOldPassWord(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        // Kiểm tra mật khẩu mới không trùng với mật khẩu cũ
        if (this.passwordEncoder.matches(reqResetPasswordDTO.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("New password must be different from old password.");
        }

        // Cập nhật mật khẩu mới
        userService.updatePassword(user.getEmail(),
                this.passwordEncoder.encode(reqResetPasswordDTO.getNewPassword()));
        return ResponseEntity.ok().body("Reset password successfully");
    }

}
