package co.project.authservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TipoDocumento {
    private Long  id;
    private String nombre;
    private boolean registroVigente;
}
