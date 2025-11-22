package co.project.authservice.repository;

import co.project.authservice.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByEmailAndRegistroVigenteTrue(String email);

    Optional<Persona> findByNumeroDocumentoAndRegistroVigenteTrue(String numeroDocumento);

    boolean existsByEmail(String email);

    boolean existsByNumeroDocumento(String numeroDocumento);
}