package co.project.authservice.service;

import co.project.authservice.dto.response.PersonaResponse;
import co.project.authservice.dto.response.UsuarioResponse;
import co.project.authservice.entity.Persona;
import co.project.authservice.entity.Usuario;
import co.project.authservice.exception.ResourceNotFoundException;
import co.project.authservice.repository.PerfilPorUsuarioRepository;
import co.project.authservice.repository.UsuarioRepository;
import co.project.authservice.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilPorUsuarioRepository perfilPorUsuarioRepository;

    @Transactional(readOnly = true)
    public UsuarioResponse getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildUsuarioResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return buildUsuarioResponse(usuario);
    }

    private UsuarioResponse buildUsuarioResponse(Usuario usuario) {
        List<String> perfiles = perfilPorUsuarioRepository
                .findActiveProfilesByUsuarioId(usuario.getId())
                .stream()
                .map(ppu -> ppu.getPerfil().getNombre())
                .collect(Collectors.toList());

        PersonaResponse personaResponse = null;
        if (usuario.getPersona() != null) {
            personaResponse = buildPersonaResponse(usuario.getPersona());
        }

        boolean passwordExpired = isPasswordExpired(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .login(usuario.getLogin())
                .email(usuario.getEmail())
                .oauthProvider(usuario.getOauthProvider())
                .emailVerificado(usuario.getEmailVerificado())
                .fechaRegistro(usuario.getFechaRegistro())
                .profilePictureUrl(usuario.getProfilePictureUrl())
                .persona(personaResponse)
                .perfiles(perfiles)
                .passwordExpired(passwordExpired)
                .build();
    }

    private PersonaResponse buildPersonaResponse(Persona persona) {
        int edad = Period.between(persona.getFechaNacimiento(), LocalDate.now()).getYears();

        return PersonaResponse.builder()
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

    private boolean isPasswordExpired(Usuario usuario) {
        if (usuario.getFechaVencimiento() == null) {
            return false;
        }
        return usuario.getFechaVencimiento().isBefore(ZonedDateTime.now());
    }

    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

}
