package co.project.authservice.domain.repository;

import co.project.authservice.domain.model.TipoDocumento;
import java.util.List;

public interface ITipoDocumento {
    TipoDocumento findBYId(Long id);
    TipoDocumento finByNombreTipoDocumento(String nombreTipoDocumento);
    List<TipoDocumento> findAll();
}
