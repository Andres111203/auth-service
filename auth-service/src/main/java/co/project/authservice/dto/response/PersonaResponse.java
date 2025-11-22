package co.project.authservice.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaResponse {

    private Long id;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String email;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String telefono;
}
