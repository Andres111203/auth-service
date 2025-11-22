package co.project.authservice.repository;

import co.project.authservice.entity.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, String> {

    Optional<TipoDocumento> findByIdAndRegistroVigenteTrue(String id);
}