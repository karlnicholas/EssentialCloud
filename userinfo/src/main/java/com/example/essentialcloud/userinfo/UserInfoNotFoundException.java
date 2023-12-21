package com.example.essentialcloud.userinfo;

import java.io.Serial;

public class UserInfoNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7428051251365675318L;

    public UserInfoNotFoundException(String message) {
        super(message);
    }
}
