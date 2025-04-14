package com.qubitbug.chat.chat_app_backend.repositories;

import com.qubitbug.chat.chat_app_backend.entities.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    UserEntity findByUsername(String username);

    Optional<UserEntity> findById(ObjectId id);

    Optional<UserEntity> findByEmail(String email);

    UserEntity findByEmailAndPassword(String email, String password);


}