package com.qubitbug.chat.chat_app_backend.entities.args;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomArgs {
    private String roomId;
    private String roomName;
}
