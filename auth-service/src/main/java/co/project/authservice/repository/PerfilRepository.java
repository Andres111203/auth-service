package co.project.authservice.repository;

import co.project.authservice.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNombreAndRegistroVigenteTrue(String nombre);

    boolean existsByNombre(String nombre);
}