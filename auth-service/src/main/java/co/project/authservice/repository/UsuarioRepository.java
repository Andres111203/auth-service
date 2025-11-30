package co.project.authservice.repository;

import co.project.authservice.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLoginAndRegistroVigenteTrue(String login);

    Optional<Usuario> findByEmailAndRegistroVigenteTrue(String email);

    @Query("SELECT u FROM Usuario u WHERE " +
            "(u.login = :identifier OR u.email = :identifier) " +
            "AND u.registroVigente = true")
    Optional<Usuario> findByLoginOrEmail(@Param("identifier") String identifier);

    Optional<Usuario> findByOauthProviderAndOauthProviderIdAndRegistroVigenteTrue(
            String oauthProvider,
            String oauthProviderId
    );

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}