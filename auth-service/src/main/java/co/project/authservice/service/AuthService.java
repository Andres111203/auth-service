package co.project.authservice.service;

import co.project.authservice.dto.request.LoginRequest;
import co.project.authservice.dto.request.RegisterRequest;
import co.project.authservice.dto.response.AuthResponse;
import co.project.authservice.dto.response.PersonaResponse;
import co.project.authservice.entity.*;
import co.project.authservice.exception.*;
import co.project.authservice.repository.*;
import co.project.authservice.security.jwt.JwtTokenProvider;
import co.project.authservice.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PerfilRepository perfilRepository;
    private final PerfilPorUsuarioRepository perfilPorUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${security.password.expiration-days}")
    private int passwordExpirationDays;

    @Value("${security.password.min-age-years}")
    private int minAgeYears;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro para usuario: {}", request.getEmail());


        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("Las contraseñas no coinciden");
        }

        validateMinimumAge(request.getFechaNacimiento());

        if (usuarioRepository.existsByLogin(request.getLogin())) {
            throw new ResourceAlreadyExistsException("El login ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("El email ya está registrado");
        }

        if (personaRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("El email de la persona ya está registrado");
        }

        if (personaRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new ResourceAlreadyExistsException("El número de documento ya está registrado");
        }

        TipoDocumento tipoDocumento = tipoDocumentoRepository
                .findByIdAndRegistroVigenteTrue(request.getTipoDocumento())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de documento no encontrado: " + request.getTipoDocumento()));

        Persona persona = Persona.builder()
                .primerNombre(request.getPrimerNombre())
                .segundoNombre(request.getSegundoNombre())
                .primerApellido(request.getPrimerApellido())
                .segundoApellido(request.getSegundoApellido())
                .tipoDocumento(tipoDocumento)
                .numeroDocumento(request.getNumeroDocumento())
                .email(request.getEmail())
                .fechaNacimiento(request.getFechaNacimiento())
                .telefono(request.getTelefono())
                .registroVigente(true)
                .build();

        persona = personaRepository.save(persona);


        ZonedDateTime now = ZonedDateTime.now();
        Usuario usuario = Usuario.builder()
                .persona(persona)
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .registroVigente(true)
                .emailVerificado(false)
                .fechaRegistro(now)
                .fechaModifPassword(now)
                .vigenciaPassword(passwordExpirationDays)
                .fechaVencimiento(now.plusDays(passwordExpirationDays))
                .build();

        usuario = usuarioRepository.save(usuario);


        assignDefaultProfile(usuario);


        return authenticateAndGenerateTokens(request.getLogin(), request.getPassword());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para: {}", request.getIdentifier());

        Usuario usuario = usuarioRepository.findByLoginOrEmail(request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));


        if (!usuario.getRegistroVigente()) {
            throw new AccountDisabledException("La cuenta está deshabilitada");
        }


        if (usuario.getOauthProvider() != null) {
            throw new InvalidCredentialsException(
                    "Esta cuenta usa autenticación OAuth2. Por favor inicie sesión con " +
                            usuario.getOauthProvider());
        }


        if (usuario.getFechaVencimiento() != null &&
                usuario.getFechaVencimiento().isBefore(ZonedDateTime.now())) {
            throw new PasswordExpiredException("La contraseña ha expirado. Por favor actualícela.");
        }

        return authenticateAndGenerateTokens(request.getIdentifier(), request.getPassword());
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token inválido o expirado");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        Usuario usuario = usuarioRepository.findByLoginOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!usuario.getRegistroVigente()) {
            throw new AccountDisabledException("La cuenta está deshabilitada");
        }

        List<String> perfiles = perfilPorUsuarioRepository
                .findActiveProfilesByUsuarioId(usuario.getId())
                .stream()
                .map(ppu -> ppu.getPerfil().getNombre())
                .collect(Collectors.toList());

        UserDetailsImpl userDetails = UserDetailsImpl.build(usuario, perfiles);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newToken = tokenProvider.generateToken(auth, usuario.getId());
        String newRefreshToken = tokenProvider.generateRefreshToken(usuario.getEmail());

        return buildAuthResponse(usuario, newToken, newRefreshToken, perfiles);
    }

    private AuthResponse authenticateAndGenerateTokens(String identifier, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByLoginOrEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = tokenProvider.generateToken(authentication, usuario.getId());
        String refreshToken = tokenProvider.generateRefreshToken(usuario.getEmail());

        List<String> perfiles = perfilPorUsuarioRepository
                .findActiveProfilesByUsuarioId(usuario.getId())
                .stream()
                .map(ppu -> ppu.getPerfil().getNombre())
                .collect(Collectors.toList());

        return buildAuthResponse(usuario, token, refreshToken, perfiles);
    }

    private AuthResponse buildAuthResponse(
            Usuario usuario,
            String token,
            String refreshToken,
            List<String> perfiles) {

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

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .userId(usuario.getId())
                .email(usuario.getEmail())
                .login(usuario.getLogin())
                .perfiles(perfiles)
                .expiresAt(tokenProvider.getExpirationDateFromToken(token))
                .persona(personaResponse)
                .profilePictureUrl(usuario.getProfilePictureUrl())
                .build();
    }

    private void validateMinimumAge(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < minAgeYears) {
            throw new InvalidRequestException(
                    String.format("Debe tener al menos %d años para registrarse. Edad actual: %d años",
                            minAgeYears, edad));
        }
    }

    private void assignDefaultProfile(Usuario usuario) {
        Perfil perfilUser = perfilRepository.findByNombreAndRegistroVigenteTrue("USER")
                .orElseGet(() -> {
                    Perfil newPerfil = Perfil.builder()
                            .nombre("USER")
                            .registroVigente(true)
                            .build();
                    return perfilRepository.save(newPerfil);
                });

        PerfilPorUsuario perfilPorUsuario = PerfilPorUsuario.builder()
                .usuario(usuario)
                .perfil(perfilUser)
                .registroVigente(true)
                .build();

        perfilPorUsuarioRepository.save(perfilPorUsuario);
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