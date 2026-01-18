package com.socialmediaapp.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad que representa una story (historia temporal de 24h) en la aplicación.
 */
@Entity
@Table(name = "stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(length = 200)
    private String caption;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        // Las stories expiran 24 horas después de ser creadas
        expiresAt = new Date(createdAt.getTime() + 24 * 60 * 60 * 1000);
    }

    /**
     * Verifica si la story sigue siendo válida (no expirada).
     */
    @Transient
    public boolean isActive() {
        return expiresAt.after(new Date());
    }
}
