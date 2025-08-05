package com.example.soup.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
