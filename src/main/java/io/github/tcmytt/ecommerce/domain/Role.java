package io.github.tcmytt.ecommerce.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT phù hợp cho mô tả ngắn gọn
    private String description;

    @Column(nullable = false)
    private Boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @JsonIgnore // Tránh tuần hoàn khi serialize JSON
    private List<Permission> permissions;

    // Một Role có thể có nhiều User
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore // Tránh tuần hoàn khi serialize JSON
    private List<User> users;
}