package co.project.authservice.security.oauth2;

import co.project.authservice.entity.Usuario;
import co.project.authservice.repository.PerfilPorUsuarioRepository;
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

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
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
            Usuario usuario = processOAuth2User(provider, oAuth2User);

            List<String> perfiles = perfilPorUsuarioRepository
                    .findActiveProfilesByUsuarioId(usuario.getId())
                    .stream()
                    .map(ppu -> ppu.getPerfil().getNombre())
                    .collect(Collectors.toList());

            UserDetailsImpl userDetails = UserDetailsImpl.build(usuario, perfiles);

            Authentication auth = new org.springframework.security.authentication
                    .UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String token = tokenProvider.generateToken(auth, usuario.getId());
            String refreshToken = tokenProvider.generateRefreshToken(usuario.getEmail());

            String targetUrl = determineTargetUrl(token, refreshToken);

            if (response.isCommitted()) {
                log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception ex) {
            log.error("Error during OAuth2 authentication", ex);
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    allowedOrigins[0] + "/login?error=oauth2_error"
            );
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

        String name = attributes.get("name") != null
                ? attributes.get("name").toString()
                : email;

        String pictureUrl = attributes.get("picture") != null
                ? attributes.get("picture").toString()
                : null;

        return usuarioRepository
                .findByOauthProviderAndOauthProviderIdAndRegistroVigenteTrue(provider, providerId)
                .map(existingUser -> {
                    existingUser.setProfilePictureUrl(pictureUrl);
                    return usuarioRepository.save(existingUser);
                })
                .orElseGet(() -> {
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

    private String determineTargetUrl(String token, String refreshToken) {
        return UriComponentsBuilder.fromUriString(allowedOrigins[0] + "/oauth2/redirect")
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
    }
}