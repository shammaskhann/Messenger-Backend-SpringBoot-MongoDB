package com.qubitbug.chat.chat_app_backend.services;

import com.qubitbug.chat.chat_app_backend.entities.Message;
import com.qubitbug.chat.chat_app_backend.repositories.RoomRepository;
import com.qubitbug.chat.chat_app_backend.entities.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = "rooms")
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Cacheable(key = "#roomId")
    public Room getRoomById(String roomId){
        return roomRepository.findByRoomId(roomId);
    }

    @CachePut(key = "#room.roomId")
    public void saveRoom(Room room){
        roomRepository.save(room);
    }

    @Cacheable(key = "'messages_' + #roomId + '_' + #page + '_' + #size")
    public List<Message> getRoomMessages(String roomId, int page, int size) {
        log.info("Fetching messages for room {} (page {}, size {}) from database", roomId, page, size);
        Room room = getRoomById(roomId);
        List<Message> messages = room.getMessages();
        int start = page * size;
        int end = Math.min(start + size, messages.size());
        return messages.subList(start, end);
    }
}
