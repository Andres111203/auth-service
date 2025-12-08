package co.project.authservice.controller;

import co.project.authservice.dto.request.LoginRequest;
import co.project.authservice.dto.request.RegisterRequest;
import co.project.authservice.dto.response.AuthResponse;
import co.project.authservice.service.AuthService;
import co.project.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register - Email: {}", request.getEmail());
        System.out.println(request);
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login - Identifier: {}", request.getIdentifier());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        log.info("POST /auth/refresh-token");

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select-profile")
    public ResponseEntity<AuthResponse> seleccionarPerfil(
            @RequestParam String perfil,
            Authentication authentication) {

        AuthResponse response = authService.changeProfile(authentication, perfil);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<?> existsById(@PathVariable Long id) {
        log.info("GET /users/exists/{} - Checking user existence", id);

        boolean exists = userService.existsById(id);

        if (exists) {
            return ResponseEntity.ok().body("{\"exists\": true}");
        } else {
            return ResponseEntity.status(404).body("{\"exists\": false}");
        }
    }

}

