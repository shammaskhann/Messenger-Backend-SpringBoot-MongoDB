package com.qubitbug.chat.chat_app_backend.repositories;

import com.qubitbug.chat.chat_app_backend.entities.FriendRequest;
import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class UserRepositoryImpl {

    @Autowired
    MongoTemplate mongoTemplate;

    public boolean friendRequestAlreadySent(String recieverId, String senderId){
        Query query = new Query();
        query.addCriteria(Criteria.where("friendRequests.senderId").is(senderId));
        query.addCriteria(Criteria.where("_id").is(recieverId));
        return mongoTemplate.exists(query, UserEntity.class);
    }
}
