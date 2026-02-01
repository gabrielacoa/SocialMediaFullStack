package com.socialmediaapp.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Entidad que representa a un usuario en la aplicación.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String profilePictureUrl;

    @Column(length = 200)
    private String bio;

    @Column
    private String profilePicture;

    // Campos para 2FA (Two-Factor Authentication)
    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> sentMessages;

    // Los mensajes recibidos se obtienen a través de Chat (user1/user2), no directamente en Message

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> sentNotifications;

    /**
     * Usuarios que siguen a este usuario (seguidores).
     * Relación many-to-many bidireccional.
     */
    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();

    /**
     * Usuarios a los que este usuario sigue.
     * Relación many-to-many bidireccional.
     */
    @ManyToMany
    @JoinTable(
        name = "user_followings",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    /**
     * Posts guardados por este usuario.
     * Relación many-to-many con Post.
     */
    @ManyToMany
    @JoinTable(
        name = "users_saved_post",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> savedPosts = new HashSet<>();
}
