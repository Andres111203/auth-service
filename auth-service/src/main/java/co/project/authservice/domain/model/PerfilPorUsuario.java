package co.project.authservice.domain.model;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PerfilPorUsuario {
    private Long id;
    private Usuario usuario;
    private Perfil perfil;
    private Usuario usuarioAsignadoPor;
    private boolean registroVigente;
    private LocalDateTime fechaAsignacion;
}
