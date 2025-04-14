package com.qubitbug.chat.chat_app_backend.services;

import com.qubitbug.chat.chat_app_backend.entities.FriendEntity;
import com.qubitbug.chat.chat_app_backend.entities.FriendRequest;
import com.qubitbug.chat.chat_app_backend.entities.UserCredentials;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import com.qubitbug.chat.chat_app_backend.repositories.UserRepository;
import com.qubitbug.chat.chat_app_backend.repositories.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    public void register(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
    }

    public void delete(ObjectId id) {
        userRepository.deleteById(id);
    }

    public UserEntity login(UserCredentials userCredentials) {
        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        return userRepository.findByEmailAndPassword(userCredentials.getEmail(), userCredentials.getPassword());
    }

    public boolean sendFriendRequest(String senderId, String receiverId) {
       try{
           UserEntity sender = userRepository.findById(new ObjectId(senderId)).orElse(null);
           UserEntity receiver = userRepository.findById(new ObjectId(receiverId)).orElse(null);
           if (userRepositoryImpl.friendRequestAlreadySent(receiverId, senderId)) {
               throw new Exception("Friend request already sent");
           }
           if(receiver.getFriends().stream().anyMatch(friend -> friend.getId().equals(sender.getId()))){
               String message = sender.getUsername() + " is already your friend";
               throw new Exception(message);
           }
           if (sender != null && receiver != null) {
               receiver.getFriendRequests().add(new FriendRequest(sender.getId(),sender.getUsername(),sender.getEmail(),LocalDateTime.now()));
               userRepository.save(receiver);
               return true;
           }
           throw new Exception("Failed to send friend request");
       }catch (Exception e){
              throw new RuntimeException(e.getMessage());
       }
    }

    public boolean cancelFriendRequest(String senderId, String receiverId) {
        UserEntity sender = userRepository.findById(new ObjectId(senderId)).orElse(null);
        UserEntity receiver = userRepository.findById(new ObjectId(receiverId)).orElse(null);

        if (sender != null && receiver != null) {
            receiver.getFriendRequests().removeIf(request -> request.getSenderId().equals(sender.getId()));
            userRepository.save(receiver);
            return true;
        }
        return false;
    }

    boolean checkFriendReqExists(UserEntity sender, UserEntity receiver) {
        if (sender != null && receiver != null) {
            return receiver.getFriendRequests().stream()
                    .anyMatch(request -> request.getSenderId().equals(sender.getId()));
        }
        return false;
    }

    public boolean acceptFriendRequest(String senderId, String receiverId) {
        UserEntity sender = userRepository.findById(new ObjectId(senderId)).orElse(null);
        UserEntity receiver = userRepository.findById(new ObjectId(receiverId)).orElse(null);


        if (sender != null && receiver != null) {
            if (!checkFriendReqExists(sender, receiver)) {
                return false;
            }
            receiver.getFriends().add(new FriendEntity(
                    sender.getId(),
                    sender.getUsername(),
                    sender.getEmail()
            ));
            receiver.getFriendRequests().removeIf(request -> request.getSenderId().equals(sender.getId()));
            sender.getFriends().add(
                    new FriendEntity(
                            receiver.getId(),
                            receiver.getUsername(),
                            receiver.getEmail()
                    )
            );
            userRepository.save(sender);
            userRepository.save(receiver);
            return true;
        }
        return false;
    }

    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(new ObjectId(id));
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

//    public List<UserEntity> getAllUsers() {
//        return userRepository.findAll();
//    }

//    public void deleteAll() {
//        userRepository.deleteAll();
//    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<FriendRequest> getFriendRequests(String id) {
        UserEntity user = userRepository.findById(new ObjectId(id)).orElse(null);
        if (user != null) {
           return user.getFriendRequests();

        }
        return null;
    }
}

