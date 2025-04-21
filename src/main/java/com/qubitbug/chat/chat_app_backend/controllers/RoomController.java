package com.qubitbug.chat.chat_app_backend.controllers;


import com.qubitbug.chat.chat_app_backend.entities.Message;
import com.qubitbug.chat.chat_app_backend.entities.Room;
import com.qubitbug.chat.chat_app_backend.entities.args.CreateRoomArgs;
import com.qubitbug.chat.chat_app_backend.repositories.RoomRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@Slf4j
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "localhost:8080")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomArgs args){
        log.info("Creating a new room with id: {}", args.getRoomId());
        if(roomRepository.findByRoomId(args.getRoomId()) != null){
            return ResponseEntity.badRequest().body("Room already exists");
        }else{
            Room room = new Room();
            room.setRoomId(args.getRoomId());
            room.setName(args.getRoomName());
            log.info("Creating a new room with id: {}", room.toString());
            Room savedRoom = roomRepository.save(room);
            log.info("Room created: {}", savedRoom.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", true, "message", "Room created successfully", "room", savedRoom.toMap()));
        }
    }
    //get room:join
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId){
        Room room = roomRepository.findByRoomId(roomId);
        if(room == null){
            return ResponseEntity.badRequest().body("Room does not exist");
        }else{

            return ResponseEntity.ok(room);
        }
    }

    //get messages of the room
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String roomId, @RequestParam(value="page",defaultValue = "0", required = false) int page, @RequestParam(value="pageSize",defaultValue = "20", required = false) int pageSize){
        Room room = roomRepository.findByRoomId(roomId);
        if(room == null){
            return ResponseEntity.badRequest().build();
        }else{
            List<Message> messages = room.getMessages();
            int totalMessages = messages.size();
            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalMessages);
            return ResponseEntity.ok(messages.subList(start, end));
        }
    }
}
