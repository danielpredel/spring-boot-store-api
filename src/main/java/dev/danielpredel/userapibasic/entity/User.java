package dev.danielpredel.userapibasic.entity;

import dev.danielpredel.userapibasic.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Setter
    private String email;

    @Setter
    private String name;

    @Setter
    private String password;

    @Setter
    private String address;

    @OneToMany(mappedBy = "user")
    @Setter
    private List<Order> orders;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    private boolean active;

    public User() {}

    public User(String name, String email, String password, String address, Role  role, boolean active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', password='" + "*".repeat(16) + "', address='" + address + "'}";
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
