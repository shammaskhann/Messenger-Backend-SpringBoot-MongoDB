package com.qubitbug.chat.chat_app_backend.entities;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "rooms")
public class Room {
    @Id
    private String id; //MongoDB ID
    private String roomId;
    private String name;
    private List<Message> messages = new ArrayList<>();


    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<Message> getMessages() {
        return messages;
    }



    public Map toMap(){
        return Map.of("roomId", roomId, "messages", messages);
    }
}