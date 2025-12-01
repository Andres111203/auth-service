package co.project.authservice.security.oauth2;

import co.project.authservice.entity.Perfil;
import co.project.authservice.entity.PerfilPorUsuario;
import co.project.authservice.entity.Usuario;
import co.project.authservice.repository.PerfilPorUsuarioRepository;
import co.project.authservice.repository.PerfilRepository;
import co.project.authservice.repository.UsuarioRepository;
import co.project.authservice.security.jwt.JwtTokenProvider;
import co.project.authservice.security.jwt.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PerfilPorUsuarioRepository perfilPorUsuarioRepository;
    private final PerfilRepository perfilRepository;

    // Usar la propiedad específica de OAuth redirect URIs
    @Value("${app.oauth2.authorized-redirect-uri}")
    private String authorizedRedirectUris;

    // opcional si sigues necesitando allowedOrigins para otra cosa
    @Value("${cors.allowed-origins:}")
    private String allowedOrigins;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        try {
            log.info("OAuth2 authentication successful for provider: {}", provider);

            Usuario usuario = processOAuth2User(provider, oAuth2User);

            List<String> perfiles = perfilPorUsuarioRepository
                    .findActiveProfilesByUsuarioId(usuario.getId())
                    .stream()
                    .map(ppu -> ppu.getPerfil().getNombre())
                    .collect(Collectors.toList());

            if (perfiles.isEmpty()) {
                assignDefaultProfile(usuario);
                perfiles = List.of("USER");
            }

            UserDetailsImpl userDetails = UserDetailsImpl.build(usuario, perfiles);

            Authentication auth = new org.springframework.security.authentication
                    .UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String token = tokenProvider.generateToken(auth, usuario.getId());
            String refreshToken = tokenProvider.generateRefreshToken(usuario.getEmail());

            log.info("Tokens generated successfully for user: {}", usuario.getEmail());

            // Toma la primera URI autorizada configurada en app.oauth2.authorized-redirect-uri
            String redirectUri = authorizedRedirectUris.split(",")[0].trim();
            String targetUrl = determineRedirectUrl(redirectUri, token, refreshToken);

            log.info("Redirecting to: {}", targetUrl);

            if (response.isCommitted()) {
                log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception ex) {
            log.error("Error during OAuth2 authentication", ex);

            String redirectUri = authorizedRedirectUris.split(",")[0].trim();
            String errorUrl = UriComponentsBuilder.fromUriString(redirectUri.replace("/oauth2/redirect.html","/login.html"))
                    .queryParam("error", "oauth2_error")
                    .queryParam("message", ex.getMessage())
                    .build()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

    private Usuario processOAuth2User(String provider, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = attributes.get("sub") != null
                ? attributes.get("sub").toString()
                : attributes.get("id").toString();

        String email = attributes.get("email") != null
                ? attributes.get("email").toString()
                : null;

        if (email == null) {
            throw new IllegalArgumentException("Email no proporcionado por el proveedor OAuth2");
        }

        String pictureUrl = attributes.get("picture") != null
                ? attributes.get("picture").toString()
                : null;

        log.info("Processing OAuth2 user - Provider: {}, Email: {}", provider, email);

        return usuarioRepository
                .findByOauthProviderAndOauthProviderIdAndRegistroVigenteTrue(provider, providerId)
                .map(existingUser -> {
                    log.info("Usuario OAuth2 existente encontrado: {}", existingUser.getEmail());
                    existingUser.setProfilePictureUrl(pictureUrl);
                    return usuarioRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("Creando nuevo usuario OAuth2: {}", email);

                    Usuario newUser = Usuario.builder()
                            .email(email)
                            .oauthProvider(provider)
                            .oauthProviderId(providerId)
                            .profilePictureUrl(pictureUrl)
                            .emailVerificado(true)
                            .registroVigente(true)
                            .fechaRegistro(ZonedDateTime.now())
                            .build();

                    return usuarioRepository.save(newUser);
                });
    }

    private void assignDefaultProfile(Usuario usuario) {
        log.info("Asignando perfil por defecto al usuario: {}", usuario.getEmail());

        Perfil perfilUser = perfilRepository.findByNombreAndRegistroVigenteTrue("USER")
                .orElseGet(() -> {
                    log.info("Creando perfil USER por defecto");
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
        log.info("Perfil USER asignado exitosamente");
    }

    private String determineRedirectUrl(String redirectUri, String token, String refreshToken) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
    }
}
