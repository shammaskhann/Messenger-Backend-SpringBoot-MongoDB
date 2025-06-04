package com.qubitbug.chat.chat_app_backend.entities;

import lombok.*;


import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchedFriendEntity implements Serializable {
    private String id;
    private String username;
    private String email;
    private String deviceToken;
    private List<FriendEntity> mutualFriends;
}
