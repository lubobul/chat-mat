package com.chat_mat_rest_service.entities;
import jakarta.persistence.*;
import lombok.*;
import java.security.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
