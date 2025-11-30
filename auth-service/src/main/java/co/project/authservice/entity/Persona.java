package co.project.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id")
    private Long id;

    @Column(name = "per_primer_nombre", length = 60, nullable = false)
    private String primerNombre;

    @Column(name = "per_segundo_nombre", length = 60)
    private String segundoNombre;

    @Column(name = "per_primer_apellido", length = 60, nullable = false)
    private String primerApellido;

    @Column(name = "per_segundo_apellido", length = 60)
    private String segundoApellido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "per_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "per_numero_documento", length = 20, nullable = false)
    private String numeroDocumento;

    @Column(name = "per_email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "per_fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "per_telefono", length = 30)
    private String telefono;

    @Column(name = "per_registro_vigente")
    private Boolean registroVigente = true;

    @OneToOne(mappedBy = "persona")
    private Usuario usuario;
}