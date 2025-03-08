package io.github.tcmytt.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.github.tcmytt.ecommerce.domain.Permission;
import io.github.tcmytt.ecommerce.domain.Role;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.repository.PermissionRepository;
import io.github.tcmytt.ecommerce.repository.RoleRepository;
import io.github.tcmytt.ecommerce.repository.UserRepository;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");

        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        // Khởi tạo Permissions nếu chưa có
        if (countPermissions == 0) {
            ArrayList<Permission> permissions = new ArrayList<>();
            // Category
            permissions.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
            permissions.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
            permissions.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            permissions.add(new Permission("Get a category by id", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            permissions
                    .add(new Permission("Get categories with pagination", "/api/v1/categories", "GET", "CATEGORIES"));

            // Coupon
            permissions.add(new Permission("Create a coupon", "/api/v1/coupons", "POST", "COUPONS"));
            permissions.add(new Permission("Update a coupon", "/api/v1/coupons", "PUT", "COUPONS"));
            permissions.add(new Permission("Delete a coupon", "/api/v1/coupons/{id}", "DELETE", "COUPONS"));
            permissions.add(new Permission("Get a coupon by id", "/api/v1/coupons/{id}", "GET", "COUPONS"));
            permissions.add(new Permission("Get coupons with pagination", "/api/v1/coupons", "GET", "COUPONS"));

            // Order
            permissions.add(new Permission("Create an order", "/api/v1/orders", "POST", "ORDERS"));
            permissions.add(new Permission("Update an order", "/api/v1/orders", "PUT", "ORDERS"));
            permissions.add(new Permission("Delete an order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
            permissions.add(new Permission("Get an order by id", "/api/v1/orders/{id}", "GET", "ORDERS"));
            permissions.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS"));

            // Product
            permissions.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS"));
            permissions.add(new Permission("Update a product", "/api/v1/products", "PUT", "PRODUCTS"));
            permissions.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
            permissions.add(new Permission("Get a product by id", "/api/v1/products/{id}", "GET", "PRODUCTS"));
            permissions.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS"));

            // Review
            permissions.add(new Permission("Create a review", "/api/v1/reviews", "POST", "REVIEWS"));
            permissions.add(new Permission("Update a review", "/api/v1/reviews", "PUT", "REVIEWS"));
            permissions.add(new Permission("Delete a review", "/api/v1/reviews/{id}", "DELETE", "REVIEWS"));
            permissions.add(new Permission("Get a review by id", "/api/v1/reviews/{id}", "GET", "REVIEWS"));
            permissions.add(new Permission("Get reviews with pagination", "/api/v1/reviews", "GET", "REVIEWS"));

            // User
            permissions.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            permissions.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            permissions.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            permissions.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            permissions.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            this.permissionRepository.saveAll(permissions);
        }

        // Khởi tạo Roles nếu chưa có
        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator role with full access");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Regular user role with limited access");
            userRole.setActive(true);
            userRole.setPermissions(allPermissions.stream()
                    .filter(p -> p.getModule().equals("PRODUCTS") || p.getModule().equals("ORDERS"))
                    .toList());

            this.roleRepository.save(adminRole);
            this.roleRepository.save(userRole);
        }

        // Khởi tạo Users nếu chưa có
        if (countUsers == 0) {
            Role adminRole = this.roleRepository.findByName("ADMIN");
            Role userRole = this.roleRepository.findByName("USER");

            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            adminUser.setGender(true);
            adminUser.setAddress("Admin Address");
            adminUser.setPhoneNumber("1234567890");
            adminUser.setRole(adminRole);

            User regularUser = new User();
            regularUser.setName("Regular User");
            regularUser.setEmail("user@gmail.com");
            regularUser.setPassword(this.passwordEncoder.encode("123456"));
            regularUser.setGender(false);
            regularUser.setAddress("User Address");
            regularUser.setPhoneNumber("0987654321");
            regularUser.setRole(userRole);

            this.userRepository.save(adminUser);
            this.userRepository.save(regularUser);
        }

        // Kiểm tra và thông báo kết quả
        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else {
            System.out.println(">>> END INIT DATABASE");
        }
    }
}