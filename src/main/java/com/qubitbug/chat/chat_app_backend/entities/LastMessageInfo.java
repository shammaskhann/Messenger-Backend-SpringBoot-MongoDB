package com.qubitbug.chat.chat_app_backend.entities;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LastMessageInfo {
    private String message;
    private LocalDateTime timestamp;
    private String senderId;
    private boolean unread;
}
