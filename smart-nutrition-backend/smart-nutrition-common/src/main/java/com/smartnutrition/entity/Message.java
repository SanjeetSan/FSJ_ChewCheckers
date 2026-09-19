package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
    private String messageText;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    public Message() {}

    public Long getId() { return id; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getMessageText() { return messageText; }
    public LocalDateTime getSentAt() { return sentAt; }

    public void setSender(User sender) { this.sender = sender; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Message m = new Message();
        public Builder sender(User v) { m.sender = v; return this; }
        public Builder receiver(User v) { m.receiver = v; return this; }
        public Builder messageText(String v) { m.messageText = v; return this; }
        public Message build() { return m; }
    }
}
