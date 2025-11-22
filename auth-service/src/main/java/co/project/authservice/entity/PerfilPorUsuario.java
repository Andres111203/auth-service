package co.project.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfilporusuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilPorUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perfpusu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfpusu_usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfpusu_perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "perfpusu_registro_vigente")
    private Boolean registroVigente = true;

    @Column(name = "perfpusu_fecha_asignacion")
    private LocalDateTime fechaAsignacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfpusu_asignado_por")
    private Usuario asignadoPor;

    @PrePersist
    protected void onCreate() {
        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
    }
}