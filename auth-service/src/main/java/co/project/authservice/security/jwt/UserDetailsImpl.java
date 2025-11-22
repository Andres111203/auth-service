package co.project.authservice.security.jwt;

import co.project.authservice.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

    public static UserDetailsImpl build(Usuario usuario, List<String> perfiles) {
        List<GrantedAuthority> authorities = perfiles.stream()
                .map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.toUpperCase()))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(usuario.getId())
                .username(usuario.getLogin() != null ? usuario.getLogin() : usuario.getEmail())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(authorities)
                .enabled(usuario.getRegistroVigente())
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();
    }
}