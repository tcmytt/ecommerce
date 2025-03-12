package io.github.tcmytt.ecommerce.domain;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private Boolean gender;

    // Chỉ lưu đường dẫn hình ảnh
    private String avatar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    private String phoneNumber;

    private String dateOfBirth;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String refreshToken;

    private String createdAt;

    private String updatedBy;

    private String updatedAt;

    // Mỗi User chỉ có một Role
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false) // Liên kết với bảng roles
    @JsonIgnore
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    // Callback trước khi persist
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().toString();
        this.updatedAt = LocalDateTime.now().toString(); // Nếu cần cập nhật cả updatedAt
    }

    // Callback trước khi update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now().toString();
    }
}