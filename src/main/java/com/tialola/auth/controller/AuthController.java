package com.tialola.auth.controller;

import com.tialola.auth.dto.LoginRequest;
import com.tialola.auth.dto.LoginResponse;
import com.tialola.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login/cajero")
    public ResponseEntity<LoginResponse> loginCajero() {
        LoginResponse response = authService.loginCajeroSinPassword();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login/dueno")
    public ResponseEntity<LoginResponse> loginDueno(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginDueno(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            authService.logout(jwtToken);
        }
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            return ResponseEntity.ok(authService.getCurrentUser(jwtToken));
        }
        return ResponseEntity.status(401).body("No autorizado");
    }
}
