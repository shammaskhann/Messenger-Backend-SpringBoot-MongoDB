package com.qubitbug.chat.chat_app_backend.services;

import com.qubitbug.chat.chat_app_backend.repositories.RoomRepository;
import com.qubitbug.chat.chat_app_backend.entities.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    public Room getRoomById(String roomId){
        return roomRepository.findByRoomId(roomId);
    }

    public void saveRoom(Room room){
        roomRepository.save(room);
    }
}
