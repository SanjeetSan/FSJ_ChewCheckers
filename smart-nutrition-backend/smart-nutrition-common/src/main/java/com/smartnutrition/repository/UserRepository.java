package com.smartnutrition.repository;

import com.smartnutrition.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_tokens WHERE user_id = :id", nativeQuery = true)
    void deleteRefreshTokensByUserId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM messages WHERE sender_id = :id OR receiver_id = :id", nativeQuery = true)
    void deleteMessagesByUserId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM parent_students WHERE parent_id = :id", nativeQuery = true)
    void deleteParentStudentsByUserId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM friendships WHERE user_id = :id OR friend_id = :id", nativeQuery = true)
    void deleteFriendshipsByUserId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
    void hardDeleteUserById(@Param("id") Long id);
}
