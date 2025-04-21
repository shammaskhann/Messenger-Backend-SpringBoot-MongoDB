package com.qubitbug.chat.chat_app_backend.controllers;


import com.qubitbug.chat.chat_app_backend.entities.LastMessageInfo;
import com.qubitbug.chat.chat_app_backend.entities.Message;
import com.qubitbug.chat.chat_app_backend.entities.Room;
import com.qubitbug.chat.chat_app_backend.payload.MessageRequest;
import com.qubitbug.chat.chat_app_backend.repositories.RoomRepository;
import com.qubitbug.chat.chat_app_backend.services.MessageProducerService;
import com.qubitbug.chat.chat_app_backend.services.MessageService;
import com.qubitbug.chat.chat_app_backend.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@Slf4j
@Controller
@CrossOrigin(origins = "localhost:8080")
public class ChatController {

 private SimpMessagingTemplate messagingTemplate;

    @Autowired
    MessageProducerService messageProducerService;

    @Autowired
    RoomService roomService;

    @Autowired
    MessageService messageService;



    public ChatController(RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;

    }
//
//    @MessageMapping("/sendMessage/{roomId}")// /app/sendMessage/roomId
//    @SendTo("/topic/room/{roomId}")//subscribe
//    public Message sendMessage(
//            @DestinationVariable String roomId,
//            @RequestBody MessageRequest request
//    ) {
//        log.info("Sending message to room {}", roomId);
//        Room room = roomRepository.findByRoomId(request.getRoomId());
//        Message message = new Message();
//        message.setContent(request.getContent());
//        message.setSender(request.getSender());
//        message.setTimeStamp(LocalDateTime.now());
//        if (room != null) {
//            room.getMessages().add(message);
//            getLastMessageInfo(roomId);
//            roomRepository.save(room);
//        } else {
//            throw new RuntimeException("room not found !!");
//        }
//
//        return message;
//    }
//
//    @MessageMapping("/getLastMessage/{roomId}")
//    @SendTo("/topic/lastMessage/{roomId}")
//    public LastMessageInfo getLastMessageInfo(String roomId) {
//        log.info("Getting last message info for room {}", roomId);
//        Room room = roomRepository.findByRoomId(roomId);
//        if (room != null) {
//            LastMessageInfo lastMessageInfo = new LastMessageInfo();
//            lastMessageInfo.setMessage(room.getMessages().get(room.getMessages().size() - 1).getContent());
//            lastMessageInfo.setTimestamp(room.getMessages().get(room.getMessages().size() - 1).getTimestamp());
//            lastMessageInfo.setSenderId(room.getMessages().get(room.getMessages().size() - 1).getSender());
//            return lastMessageInfo;
//        } else {
//            throw new RuntimeException("room not found !!");
//        }
//    }

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request
    ) {
        log.info("Queueing message for room {}", roomId);

        if (roomService.getRoomById(roomId) == null) {
            throw new RuntimeException("Room not found!");
        }

        request.setRoomId(roomId);

        messageProducerService.sendMessage(request, roomId);
    }

//@MessageMapping("/sendMessage/{roomId}") // Client sends to /app/sendMessage/{roomId}
//public void sendMessage(
//        @DestinationVariable String roomId,
//        @RequestBody MessageRequest request
//) {
//
//    log.info("Sending message to room {}", roomId);
//
//
//    Room room = roomService.getRoomById(roomId);
//    if (room == null) {
//        throw new RuntimeException("room not found !!");
//    }
//
//    Message message = new Message();
//    message.setContent(request.getContent());
//    message.setSender(request.getSender());
//    message.setTimeStamp(LocalDateTime.now());
//    message.setRead(false);
//    // Save message to DB
//    room.getMessages().add(message);
//    roomService.saveRoom(room);
//
//    // ✅ Send full message to chat room subscribers
//    messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
//
//    // ✅ Send last message info to home screen subscribers
//    LastMessageInfo lastMessage = new LastMessageInfo();
//    lastMessage.setMessage(message.getContent());
//    lastMessage.setSenderId(message.getSender());
//    lastMessage.setTimestamp(message.getTimestamp());
//    lastMessage.setUnread(true); // If you have unread logic
//
//    messagingTemplate.convertAndSend("/topic/lastMessage/" + roomId, lastMessage);
//}

    // Optional: Manually fetch last message if needed (used rarely now)
    @MessageMapping("/getLastMessage/{roomId}") // /app/getLastMessage/{roomId}
    @SendTo("/topic/lastMessage/{roomId}")
    public LastMessageInfo getLastMessageInfo(@DestinationVariable String roomId) {
        log.info("Getting last message info for room {}", roomId);
        Room room = roomService.getRoomById(roomId);
        if (room == null || room.getMessages().isEmpty()) {
            throw new RuntimeException("room not found or has no messages!");
        }

        Message last = room.getMessages().get(room.getMessages().size() - 1);
        log.info("Getting last message info for room {}", last.toString());
        LastMessageInfo info = new LastMessageInfo();
        info.setMessage(last.getContent());
        info.setTimestamp(last.getTimestamp());
        info.setSenderId(last.getSender());
        info.setUnread(true);
        return info;
    }
}
