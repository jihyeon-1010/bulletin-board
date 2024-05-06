package com.example.bulletinboardapp.dto.user.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserRequest {
    private String nickname;
    private String email;
    private String password;
}
