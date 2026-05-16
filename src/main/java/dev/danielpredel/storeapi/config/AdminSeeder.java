package dev.danielpredel.storeapi.config;

import dev.danielpredel.storeapi.user.entity.User;
import dev.danielpredel.storeapi.enums.Role;
import dev.danielpredel.storeapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner createAdmin() {
        return args -> {

            String name = "Administrator";
            String email = adminEmail;
            String password = passwordEncoder.encode(adminPassword);
            String address = "No Need For An Address";
            Role role = Role.ADMIN;

            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = new User(name, email, password, address, role, true);

                userRepository.save(admin);

                System.out.println("Admin user created");
            }
        };
    }
}
