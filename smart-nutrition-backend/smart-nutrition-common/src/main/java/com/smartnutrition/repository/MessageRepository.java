package com.smartnutrition.repository;

import com.smartnutrition.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN FETCH m.sender JOIN FETCH m.receiver WHERE " +
           "(m.sender.id = :u1 AND m.receiver.id = :u2) OR " +
           "(m.sender.id = :u2 AND m.receiver.id = :u1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findChatHistory(@Param("u1") Long userId1, @Param("u2") Long userId2);
}
