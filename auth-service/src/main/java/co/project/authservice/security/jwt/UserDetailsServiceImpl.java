package co.project.authservice.security.jwt;

import co.project.authservice.entity.Usuario;
import co.project.authservice.repository.PerfilPorUsuarioRepository;
import co.project.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilPorUsuarioRepository perfilPorUsuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLoginOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con username: " + username));

        List<String> perfiles = perfilPorUsuarioRepository
                .findActiveProfilesByUsuarioId(usuario.getId())
                .stream()
                .map(ppu -> ppu.getPerfil().getNombre())
                .collect(Collectors.toList());

        return UserDetailsImpl.build(usuario, perfiles);
    }
}