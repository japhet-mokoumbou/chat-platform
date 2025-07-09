package com.example.chatplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatplatform.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Méthode pour trouver un utilisateur par son nom d'utilisateur
    Optional<User> findByUsername(String username);
    
    // Méthode pour trouver un utilisateur par son email
    Optional<User> findByEmail(String email);
    
    // Méthode pour vérifier si un utilisateur existe par son nom d'utilisateur
    boolean existsByUsername(String username);
    
    // Méthode pour vérifier si un utilisateur existe par son email
    boolean existsByEmail(String email);

}
