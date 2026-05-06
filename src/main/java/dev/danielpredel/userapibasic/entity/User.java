package dev.danielpredel.userapibasic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public User() {}

    public User(String name, String email, String password, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', password='" + "*".repeat(16) + "', address='" + address + "'}";
    }
}
