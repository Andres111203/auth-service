package co.project.authservice.controller;

import co.project.authservice.dto.response.AuthResponse;
import co.project.authservice.dto.response.PersonaResponse;
import co.project.authservice.entity.Persona;
import co.project.authservice.entity.Usuario;
import co.project.authservice.exception.ResourceNotFoundException;
import co.project.authservice.repository.PerfilPorUsuarioRepository;
import co.project.authservice.repository.UsuarioRepository;
import co.project.authservice.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserOauthController {
    private final UsuarioRepository usuarioRepository;
    private final PerfilPorUsuarioRepository perfilPorUsuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        log.info("GET /user/me - Obteniendo información del usuario autenticado");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<String> perfiles = perfilPorUsuarioRepository
                .findActiveProfilesByUsuarioId(usuario.getId())
                .stream()
                .map(ppu -> ppu.getPerfil().getNombre())
                .collect(Collectors.toList());

        PersonaResponse personaResponse = null;
        if (usuario.getPersona() != null) {
            Persona persona = usuario.getPersona();
            int edad = Period.between(persona.getFechaNacimiento(), LocalDate.now()).getYears();

            personaResponse = PersonaResponse.builder()
                    .id(persona.getId())
                    .primerNombre(persona.getPrimerNombre())
                    .segundoNombre(persona.getSegundoNombre())
                    .primerApellido(persona.getPrimerApellido())
                    .segundoApellido(persona.getSegundoApellido())
                    .nombreCompleto(buildFullName(persona))
                    .tipoDocumento(persona.getTipoDocumento().getNombre())
                    .numeroDocumento(persona.getNumeroDocumento())
                    .email(persona.getEmail())
                    .fechaNacimiento(persona.getFechaNacimiento())
                    .edad(edad)
                    .telefono(persona.getTelefono())
                    .build();
        }

        AuthResponse response = AuthResponse.builder()
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .login(usuario.getLogin())
                .perfiles(perfiles)
                .persona(personaResponse)
                .profilePictureUrl(usuario.getProfilePictureUrl())
                .build();

        return ResponseEntity.ok(response);
    }

    private String buildFullName(Persona persona) {
        StringBuilder fullName = new StringBuilder();
        fullName.append(persona.getPrimerNombre());

        if (persona.getSegundoNombre() != null && !persona.getSegundoNombre().isEmpty()) {
            fullName.append(" ").append(persona.getSegundoNombre());
        }

        fullName.append(" ").append(persona.getPrimerApellido());

        if (persona.getSegundoApellido() != null && !persona.getSegundoApellido().isEmpty()) {
            fullName.append(" ").append(persona.getSegundoApellido());
        }

        return fullName.toString();
    }
}
