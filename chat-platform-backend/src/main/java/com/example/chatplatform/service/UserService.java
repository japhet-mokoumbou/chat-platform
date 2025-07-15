package com.example.chatplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatplatform.dto.AuthResponse;
import com.example.chatplatform.dto.LoginRequest;
import com.example.chatplatform.dto.RegisterRequest;
import com.example.chatplatform.dto.UpdateProfileRequest;
import com.example.chatplatform.dto.UpdateSettingsRequest;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.util.JwtUtil;
import com.example.chatplatform.util.XmlUtils;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private XmlUtils xmlUtils;

    private static final String USERS_XML_FILE = "users.xml";

    /**
     * Inscription d'un nouvel utilisateur
     */
    public AuthResponse registerUser(RegisterRequest request) {
        // Vérifier l'unicité du nom d'utilisateur
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }

        // Vérifier l'unicité de l'email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Sauvegarder en base de données
        User savedUser = userRepository.save(user);

        // Sauvegarder en XML
        saveUsersToXml();

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getEmail(), savedUser.getId());

        return new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), "Inscription réussie");
    }

    /**
     * Connexion d'un utilisateur
     */
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
            );

            // Récupérer l'utilisateur
            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Générer le token JWT
            String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getId());

            return new AuthResponse(token, user.getUsername(), user.getEmail(), "Connexion réussie");

        } catch (AuthenticationException e) {
            throw new RuntimeException("Identifiants incorrects");
        }
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        User saved = userRepository.save(user);
        saveUsersToXml();
        return saved;
    }

    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public User updateSettings(Long userId, UpdateSettingsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (request.getNotificationsEnabled() != null) user.setNotificationsEnabled(request.getNotificationsEnabled());
        if (request.getTheme() != null) user.setTheme(request.getTheme());
        User saved = userRepository.save(user);
        saveUsersToXml();
        return saved;
    }

    public User getSettings(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Sauvegarde tous les utilisateurs en XML
     */
    private void saveUsersToXml() {
        try {
            System.out.println("=== DEBUT SAUVEGARDE XML ===");
            List<User> users = userRepository.findAll();
            System.out.println("Nombre d'utilisateurs à sauvegarder: " + users.size());

            xmlUtils.saveToXml(users, USERS_XML_FILE, User.class);
            System.out.println("Sauvegarde XML réussie: " + USERS_XML_FILE);
            System.out.println("=== FIN SAUVEGARDE XML ===");
        } catch (Exception e) {
            System.err.println("=== ERREUR SAUVEGARDE XML ===");
            System.err.println("Erreur lors de la sauvegarde XML des utilisateurs: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== FIN ERREUR XML ===");
        }
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
