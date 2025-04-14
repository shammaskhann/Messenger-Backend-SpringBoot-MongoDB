package com.qubitbug.chat.chat_app_backend.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendRequest {
    private String senderId;
    private String senderUsername;
    private String senderEmail;
    private LocalDateTime sentAt;
}
