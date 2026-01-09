package com.example.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.service.UserService;

@RestController
@RequestMapping("/internal/users")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserResponseDto> getUser(
        @PathVariable("userId") Long userId
    ) {
        UserResponseDto userResponseDto = userService.getUser(userId);
        return ResponseEntity.ok(userResponseDto);
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUersByIds(
        @RequestParam("ids") List<Long> ids
    ) {
        List<UserResponseDto> userResponseDtos = userService.getUsersByIds(ids);
        return ResponseEntity.ok(userResponseDtos);
    }

    @PostMapping("activity-score/add")
    public ResponseEntity<Void> addActivityScore(
        @RequestBody AddActivityScoreRequestDto addActivityScoreRequestDto
    ) {
        userService.addActivityScore(addActivityScoreRequestDto);
        return ResponseEntity.noContent().build();
    }
    
}
