package co.project.authservice.dto.response;

import lombok.*;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String login;
    private String email;
    private String oauthProvider;
    private Boolean emailVerificado;
    private ZonedDateTime fechaRegistro;
    private String profilePictureUrl;
    private PersonaResponse persona;
    private List<String> perfiles;
    private Boolean passwordExpired;
}