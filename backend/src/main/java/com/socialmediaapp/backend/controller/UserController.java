package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.user.UpdateProfileRequest;
import com.socialmediaapp.backend.dto.response.PostDto;
import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.service.PostService;
import com.socialmediaapp.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.socialmediaapp.backend.service.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para manejar las solicitudes relacionadas con los usuarios.
 * El registro de usuarios está en AuthController.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null ? user.getId() : null;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getMyProfile() {
        User user = getAuthenticatedUser();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("name", user.getUsername());
        profile.put("bio", user.getBio());
        profile.put("profileImage", user.getProfilePictureUrl() != null ?
            user.getProfilePictureUrl() : user.getProfilePicture());
        profile.put("website", null);
        profile.put("postsCount", user.getPosts() != null ? user.getPosts().size() : 0);
        profile.put("followersCount", user.getFollowers() != null ? user.getFollowers().size() : 0);
        profile.put("followingCount", user.getFollowing() != null ? user.getFollowing().size() : 0);

        return ResponseEntity.ok(profile);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) MultipartFile profileImage) {

        User user = getAuthenticatedUser();

        if (username != null && !username.isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (bio != null) {
            user.setBio(bio);
        }

        // Subir imagen de perfil si se proporciona
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(profileImage, "social-media/profiles");
            user.setProfilePictureUrl(imageUrl);
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Perfil actualizado exitosamente");
        response.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "bio", user.getBio() != null ? user.getBio() : "",
            "profileImage", user.getProfilePictureUrl() != null ?
                user.getProfilePictureUrl() : user.getProfilePicture()
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Sube imagen de perfil del usuario autenticado.
     */
    @PostMapping("/profile/image")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestParam("image") MultipartFile image) {

        User user = getAuthenticatedUser();
        String imageUrl = cloudinaryService.uploadImage(image, "social-media/profiles");
        user.setProfilePictureUrl(imageUrl);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Imagen de perfil actualizada");
        response.put("imageUrl", imageUrl);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene el perfil completo de un usuario.
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        Long currentUserId = getAuthenticatedUserId();

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("name", user.getUsername());
        profile.put("bio", user.getBio());
        profile.put("profileImage", user.getProfilePictureUrl());
        profile.put("website", null);
        profile.put("postsCount", postService.getPostsByUserId(id).size());
        profile.put("followersCount", 0);
        profile.put("followingCount", 0);
        profile.put("isFollowedByCurrentUser", false);

        return ResponseEntity.ok(profile);
    }

    /**
     * Obtiene los posts de un usuario.
     */
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Long id) {
        Long currentUserId = getAuthenticatedUserId();
        List<PostDto> posts = postService.getPostsByUserId(id, currentUserId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Obtiene un usuario por username.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene todos los usuarios.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Actualiza el perfil de un usuario.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserDto user = userService.updateProfile(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * Elimina un usuario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
