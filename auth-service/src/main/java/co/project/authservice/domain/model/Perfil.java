package co.project.authservice.domain.model;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Perfil {
    private Long id;
    private String nombre;
    private boolean registroVigente;
}
