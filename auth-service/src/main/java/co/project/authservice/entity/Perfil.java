package co.project.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perf_id")
    private Long id;

    @Column(name = "perf_nombre", length = 30, nullable = false, unique = true)
    private String nombre;

    @Column(name = "perf_registro_vigente")
    private Boolean registroVigente = true;

    @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PerfilPorUsuario> usuarios = new HashSet<>();
}