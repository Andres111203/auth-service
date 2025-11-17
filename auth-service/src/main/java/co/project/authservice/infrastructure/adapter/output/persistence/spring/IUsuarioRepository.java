package co.project.authservice.infrastructure.adapter.output.persistence.spring;

import co.project.authservice.infrastructure.adapter.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByNumeroDocumento(String numeroDocumento);
    Optional<UsuarioEntity> findByLogin(String login);
    Optional<UsuarioEntity> findByEmailAndLogin(String email, String login);
    void deleteById(Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByLoginAndIdNot(String login, Long id);
}
