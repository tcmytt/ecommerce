package io.github.tcmytt.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Role;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.response.ResCreateUserDTO;
import io.github.tcmytt.ecommerce.repository.UserRepository;

@Service
public class UserService {
    private final RoleService roleService;
    private final UserRepository userRepository;

    public UserService(RoleService roleService, UserRepository userRepository) {
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);
    }

    public User handleCreateUser(User user) {
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

}
