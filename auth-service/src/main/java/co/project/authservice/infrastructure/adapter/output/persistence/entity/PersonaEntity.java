package co.project.authservice.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Entity
@Table( name = "persona")
@Getter
@Setter


public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long per_id;

    @Column(nullable = false, length = 60)
    private String per_primer_nombre;

    @Column(nullable = false, length = 60)
    private String per_primer_apellido;

    @Column(nullable = true, length = 60)
    private String per_segundo_nombre;

    @Column(nullable = true, length = 60)
    private String per_segundo_apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String per_email;

    @OneToOne
    @JoinColumn(name = "per_tipo_documento", referencedColumnName = "tipodoc_id", nullable = false)
    private TipoDocumentoEntity per_tipo_documento;

    @Column(nullable = false, length = 20)
    private String per_numero_documento;

    @Column
    private LocalDate per_fecha_nacimiento;

    @Column(nullable = true, length = 30)
    private String per_telefono;

    @Column(nullable = false)
    private boolean per_registro_vigente;

}
