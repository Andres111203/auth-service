package co.project.authservice.infrastructure.adapter.output.persistence.entity;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "perfil")
@Getter
@Setter
public class PerfilEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long perf_id;

    @Column(nullable = false, unique = true, length = 30)
    @NotBlank(message = "El nombre del perfil no puede estar vacío")
    private String perf_nombre;

    @Column(nullable = false)
    private String perf_registro_vigente;

}
