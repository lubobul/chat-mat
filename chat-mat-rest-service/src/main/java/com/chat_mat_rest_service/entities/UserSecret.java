package com.chat_mat_rest_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "user_secrets")
@Filter(
        name = "nonDeletedEntityFilter",
        condition = "deleted = :isDeleted"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean deleted = false;
}