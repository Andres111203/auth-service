package co.project.authservice.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(
        name = "perfilporusuario",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"perfpusu_usuario_id", "perfpusu_perfil_id"})
        }
)
@Getter
@Setter

public class PerfilPorUsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long perfpusu_id;

    @ManyToOne
    @JoinColumn(name = "perfpusu_usuario_id", referencedColumnName = "usu_id", nullable = false)
    private UsuarioEntity perfpusu_usuario;

    @ManyToOne
    @JoinColumn(name = "perfpusu_perfil_id", referencedColumnName = "perf_id", nullable = false)
    private PerfilEntity perfpusu_perfil;

    @Column(nullable = false)
    private boolean perfpusu_registro_vigente = true;

    @Column(nullable = false)
    private LocalDateTime perfpusu_fecha_asignacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "perfpusu_asignado_por", referencedColumnName = "usu_id")
    private UsuarioEntity perfpusu_asignado_por;
}
