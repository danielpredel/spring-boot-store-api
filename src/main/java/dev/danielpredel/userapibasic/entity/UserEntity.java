package dev.danielpredel.userapibasic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Column(unique = true)
    @Setter
    private String email;

    @Setter
    private String password;

    @Setter
    private String address;

    public UserEntity() {}

    public UserEntity(String name, String email, String password, String address) {
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
