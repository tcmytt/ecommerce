package io.github.tcmytt.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.tcmytt.ecommerce.domain.Role;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.response.ResCreateUserDTO;
import io.github.tcmytt.ecommerce.repository.RoleRepository;
import io.github.tcmytt.ecommerce.repository.UserRepository;
import io.github.tcmytt.ecommerce.util.SecurityUtil;

@Service
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityUtil securityUtil;

    public UserService(RoleService roleService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            SecurityUtil securityUtil) {
        this.roleRepository = roleRepository;
        this.securityUtil = securityUtil;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public Page<User> searchUsersWithPagination(String keyword, Pageable pageable) {
        return userRepository.searchUsersWithPagination(keyword, pageable);
    }

    public User handleCreateUser(User user) {
        user.setAvatar("/upload/avatars/defaultAvatar.png");
        Role r = this.roleService.fetchByName("USER");
        user.setRole(r);
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setRole(roleService.fetchByName("USER"));
        res.setAddress(user.getAddress());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setDateOfBirth(user.getDateOfBirth());

        return res;
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public Page<User> fetchAllWithPaginationAndSorting(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public User fetchById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User findOrCreateUserByEmail(String email, String name) {
        // Kiểm tra xem người dùng đã tồn tại chưa
        Boolean isExist = userRepository.existsByEmail(email);
        if (isExist) {
            return userRepository.findByEmail(email);
        }

        // Tạo người dùng mới nếu chưa tồn tại
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPassword(""); // Không cần password vì đăng nhập bằng OAuth2
        newUser.setAvatar("/upload/avatars/defaultAvatar.png");
        Role r = this.roleService.fetchByName("USER");
        newUser.setRole(r);
        return userRepository.save(newUser);
    }

    public boolean isCurrentUserAdmin() {
        User user = securityUtil.getCurrentUser();
        return roleRepository.isAdminRole(user.getRole());
    }

    public boolean isCurrentUserUser() {
        User user = securityUtil.getCurrentUser();
        return roleRepository.isUserRole(user.getRole());
    }

    public User getCurrentUser() {
        return securityUtil.getCurrentUser();
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }
}
