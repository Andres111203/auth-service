package co.project.authservice.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Persona {
    private Long idPersona;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String email;
    private String fechaNacimiento;
    private boolean registroVigente;
}
