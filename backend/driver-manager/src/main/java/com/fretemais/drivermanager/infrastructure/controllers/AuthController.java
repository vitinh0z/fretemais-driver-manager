package com.fretemais.drivermanager.infrastructure.controllers;

import com.fretemais.drivermanager.application.dtos.LoginRequestDTO;
import com.fretemais.drivermanager.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO login) {

        if ("admin".equals(login.username()) && "123456".equals(login.password())) {
            String token = jwtTokenProvider.genereateToken(login.username());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).build();
    }
}
