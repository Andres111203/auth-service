package co.project.authservice.dto.response;


import lombok.*;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String login;
    private List<String> perfiles;
    private ZonedDateTime expiresAt;
    private PersonaResponse persona;
    private String profilePictureUrl;
}
