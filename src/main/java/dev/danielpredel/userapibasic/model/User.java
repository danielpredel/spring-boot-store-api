package dev.danielpredel.userapibasic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User {
    private final Long id;
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private String address;

    public User(Long id, String name, String email, String password, String address) {
        this.id = id;
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
