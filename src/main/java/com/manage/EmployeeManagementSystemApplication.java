package com.manage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.manage.model.User;
import com.manage.repo.UserRepo;

@SpringBootApplication
public class EmployeeManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementSystemApplication.class, args);
	}

	@Bean
	CommandLineRunner createAdmin(UserRepo repo,
	                              BCryptPasswordEncoder encoder) {

	    return args -> {

	        if (!repo.existsByEmail("borhadeparth24@gmail.com")) {

	            User admin = new User();

	            admin.setName("Parth");
	            admin.setEmail("borhadeparth24@gmail.com");
	            admin.setPassword(
	                encoder.encode("Admin@123")
	            );
	            admin.setRole("ADMIN");

	            repo.save(admin);

	            System.out.println(
	                "ADMIN CREATED SUCCESSFULLY"
	            );
	        }
	    };
	}
}
