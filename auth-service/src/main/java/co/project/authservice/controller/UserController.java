package co.project.authservice.controller;
import co.project.authservice.dto.response.UsuarioResponse;
import co.project.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> getCurrentUser(Authentication authentication) {
        log.info("GET /users/me - User: {}", authentication.getName());
        UsuarioResponse response = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Admin access", id);
        UsuarioResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
}