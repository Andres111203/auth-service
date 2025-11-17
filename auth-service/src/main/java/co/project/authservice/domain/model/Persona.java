package co.project.authservice.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.LocalDateTime;
import java.time.Period;


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

    public Boolean validateAge(LocalDateTime fechaNacimiento){

        if(fechaNacimiento.isAfter(LocalDateTime.now())){
            throw new IllegalArgumentException("Fecha inválida. Por favor valide la fecha de nacimiento");
        }
        int edad = Period.between(fechaNacimiento.toLocalDate(),
                LocalDateTime.now().toLocalDate()).getYears();
        return edad >= 16;
    }
}
