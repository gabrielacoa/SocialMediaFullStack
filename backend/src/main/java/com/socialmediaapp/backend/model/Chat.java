package com.socialmediaapp.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidad que representa un chat (conversación) entre dos usuarios.
 */
@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMessageAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        lastMessageAt = createdAt;
    }

    /**
     * Verifica si un usuario participa en este chat.
     */
    @Transient
    public boolean hasUser(Long userId) {
        return (user1 != null && user1.getId().equals(userId)) ||
               (user2 != null && user2.getId().equals(userId));
    }

    /**
     * Obtiene el otro usuario del chat (no el actual).
     */
    @Transient
    public User getOtherUser(Long currentUserId) {
        if (user1 != null && user1.getId().equals(currentUserId)) {
            return user2;
        }
        return user1;
    }
}
