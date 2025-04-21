package com.qubitbug.chat.chat_app_backend.services;


import com.qubitbug.chat.chat_app_backend.config.RabbitMQConfig;
import com.qubitbug.chat.chat_app_backend.entities.Message;
import com.qubitbug.chat.chat_app_backend.entities.wrapper.MessageWrapper;
import com.qubitbug.chat.chat_app_backend.payload.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MessageRequest message, String roomId) {
        try {
            log.info("Sending message to queue for room {}: {}", roomId, message);

            // Create a wrapper object
            MessageWrapper wrapper = new MessageWrapper();
            wrapper.setMessageRequest(message);
            wrapper.setRoomId(roomId);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    wrapper
            );
            log.info("Message successfully queued for room {}", roomId);
        } catch (Exception e) {
            log.error("Failed to queue message for room {}: {}", roomId, e.getMessage());
            throw new RuntimeException("Failed to send message");
        }
    }
}
