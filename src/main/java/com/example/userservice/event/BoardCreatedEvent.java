package com.example.userservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BoardCreatedEvent {
    private Long userId;

    public static BoardCreatedEvent fromJson(String json) {
        try {
          ObjectMapper objectMapper = new ObjectMapper();
          return objectMapper.readValue(json, BoardCreatedEvent.class);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("JSON 파싱 실패");
        }
      }

    public BoardCreatedEvent() {
    }

    public BoardCreatedEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
