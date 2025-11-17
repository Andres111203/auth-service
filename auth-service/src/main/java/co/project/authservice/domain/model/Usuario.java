package co.project.authservice.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {
    private Long id;
    private Persona idPersona;
    private String login;
    private String password;
    private String email;
    private TipoAutenticacion tipoAutenticacion;
    private boolean registroVigente;
    private Boolean emailVerificado;
    private LocalDateTime fechaVencimiento;
    private Integer vigenciaPassword;
    private LocalDateTime fechaModifPassword;
    private LocalDateTime fechaRegistro;

    public boolean isExpired(){
        if(fechaVencimiento == null) return false;
        return fechaVencimiento.isBefore(LocalDateTime.now());
    }

}
