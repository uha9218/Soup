package com.example.soup.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String passwordHash;
	private String username;
	private String role;
	private LocalDateTime createdAt;

	public static User create(String email, String passwordHash, String username, String role) {
		User user = new User();
		user.email = email;
		user.passwordHash = passwordHash;
		user.username = username;
		user.role = role;
		user.createdAt = LocalDateTime.now();
		return user;
	}

	public void update(String email, String passwordHash, String username, String role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.username = username;
		this.role = role;
	}
}

