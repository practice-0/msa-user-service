package com.example.userservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.userservice.dto.AddPointsRequestDto;

@Component
public class PointClient {
    private final RestClient restClient;

    public PointClient(
        @Value("${client.point-service.url}") String pointServiceUrl
    ) {
        this.restClient = RestClient.builder().baseUrl(pointServiceUrl).build();
    }
    
    public void addPoints(Long userId, int amount) {
        AddPointsRequestDto addPointsRequestDto = new AddPointsRequestDto(
            userId,
            amount
        );
        this.restClient.post()
            .uri("/internal/points/add")
            .contentType(MediaType.APPLICATION_JSON)
            .body(addPointsRequestDto)
            .retrieve()
            .toBodilessEntity();
    }
}
