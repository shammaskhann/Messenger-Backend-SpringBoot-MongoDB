package com.qubitbug.chat.chat_app_backend.entities.wrapper;

import com.qubitbug.chat.chat_app_backend.payload.MessageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper implements Serializable {
    private MessageRequest messageRequest;
    private String roomId;
}