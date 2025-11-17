package co.project.authservice.infrastructure.adapter.output.persistence.spring;

import co.project.authservice.infrastructure.adapter.output.persistence.entity.PersonaEntity;
import co.project.authservice.infrastructure.adapter.output.persistence.entity.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPersonaRepository extends JpaRepository<PersonaEntity, Long> {
    Optional<PersonaEntity> findByNumeroDocumento(String numeroDocumento);
    Optional<PersonaEntity> findByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByEmail(String email);
    Optional<PersonaEntity> findByIdPersona(String idPersona);
    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
