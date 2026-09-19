package com.smartnutrition.service;

import com.smartnutrition.dto.request.SendMessageRequest;
import com.smartnutrition.dto.response.MessageResponse;
import com.smartnutrition.entity.Message;
import com.smartnutrition.entity.User;
import com.smartnutrition.repository.MessageRepository;
import com.smartnutrition.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessageResponse sendMessage(String senderEmail, SendMessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender user not found"));

        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver user not found with ID: " + request.receiverId()));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .messageText(request.messageText())
                .build();

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getChatHistory(String email, Long contactId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!userRepository.existsById(contactId)) {
            throw new IllegalArgumentException("Contact user not found with ID: " + contactId);
        }

        List<Message> history = messageRepository.findChatHistory(user.getId(), contactId);
        return history.stream().map(this::mapToResponse).toList();
    }

    private MessageResponse mapToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getReceiver().getId(),
                message.getReceiver().getName(),
                message.getMessageText(),
                message.getSentAt()
        );
    }
}
