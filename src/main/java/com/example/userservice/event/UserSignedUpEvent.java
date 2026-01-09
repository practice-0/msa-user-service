package com.example.userservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSignedUpEvent {
    private Long userId;
    private String name;
    public UserSignedUpEvent() {
    }
    public UserSignedUpEvent(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    public Long getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 직렬화 실패");
        }
    }
}
