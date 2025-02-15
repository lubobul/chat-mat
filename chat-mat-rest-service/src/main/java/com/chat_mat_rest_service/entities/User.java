package com.chat_mat_rest_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "users")
@Filter(
        name = "nonDeletedEntityFilter",
        condition = "deleted = :isDeleted"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(columnDefinition = "TEXT")
    private String avatar; // Base64 encoded image

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> ownedChats;

    @ManyToMany(mappedBy = "participants")
    private List<Chat> chats;
}
