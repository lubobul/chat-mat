package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndIdNot(
            String username, String email, Long id, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseAndIdNot(String username, Long id, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCaseAndIdNot(String email, Long id, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(String username, String email, Pageable pageable);
    Page<User> findByIdNot(Long id, Pageable pageable);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);;

    @Override
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findById(@Param("id") Long id);
}
