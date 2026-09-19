package com.smartnutrition.repository;

import com.smartnutrition.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :u1 AND f.user2.id = :u2) OR (f.user1.id = :u2 AND f.user2.id = :u1)")
    Optional<Friendship> findRelation(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.user1 JOIN FETCH f.user2 WHERE " +
           "(f.user1.id = :u OR f.user2.id = :u) AND f.status = 'ACCEPTED'")
    List<Friendship> findFriends(@Param("u") Long userId);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.user1 JOIN FETCH f.user2 WHERE " +
           "f.user2.id = :u AND f.status = 'PENDING'")
    List<Friendship> findIncomingRequests(@Param("u") Long userId);
}
