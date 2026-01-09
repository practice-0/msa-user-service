package com.example.userservice.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.userservice.client.PointClient;
import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import com.example.userservice.dto.AddActivityScoreRequestDto;
import com.example.userservice.dto.LoginRequestDto;
import com.example.userservice.dto.LoginResponseDto;
import com.example.userservice.dto.SignUpRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.event.UserSignedUpEvent;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PointClient pointClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String jwtSecret;

    public UserService(
        UserRepository userRepository,
        PointClient pointClient,
        KafkaTemplate<String, String> kafkaTemplate,
        @Value("${jwt.secret}") String jwtSecret
    ) {
        this.userRepository = userRepository;
        this.pointClient = pointClient;
        this.kafkaTemplate = kafkaTemplate;
        this.jwtSecret = jwtSecret;
    }

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        User user = new User(
            signUpRequestDto.getEmail(),
            signUpRequestDto.getName(),
            signUpRequestDto.getPassword()
        );

        User savedUser = this.userRepository.save(user);

        this.pointClient.addPoints(savedUser.getUserId(), 1000);

        UserSignedUpEvent userSignedUpEvent = new UserSignedUpEvent(
            savedUser.getUserId(),
            savedUser.getName()
        );

        kafkaTemplate.send("user.signed-up", userSignedUpEvent.toJsonString());
    }

    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new UserResponseDto(
            user.getUserId(),
            user.getEmail(),
            user.getName()
        );
    }

    public List<UserResponseDto> getUsersByIds(List<Long> ids) {
        List<User> users = this.userRepository.findAllById(ids);
        return users.stream()
            .map(user -> new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getName()
            ))
            .toList();
    }

    @Transactional
    public void addActivityScore(AddActivityScoreRequestDto addActivityScoreRequestDto) {
        User user = this.userRepository.findById(addActivityScoreRequestDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.addActivityScore(addActivityScoreRequestDto.getScore());

        this.userRepository.save(user);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = this.userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!user.getPassword().equals(loginRequestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // jwt
        SecretKey secretKey = Keys.hmacShaKeyFor(
            jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
            .subject(user.getUserId().toString())
            .signWith(secretKey)
            .compact();

        return new LoginResponseDto(
            token
        );
    }
}
