package com.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.auth_service.repository.UserRepository;
import com.auth_service.model.User;
import com.auth_service.model.Role;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail("admin@technova.com").isEmpty()) {
				User admin = new User(
					"admin",
					passwordEncoder.encode("admin123"),
					Role.ADMIN,
					null,
					"admin@technova.com"
				);
				admin.setApproved(true);
				userRepository.save(admin);
				System.out.println("Admin user seeded successfully.");
			}
		};
	}
}
