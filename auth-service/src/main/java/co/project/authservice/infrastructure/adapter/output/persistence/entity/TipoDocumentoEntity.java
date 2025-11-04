package co.project.authservice.infrastructure.adapter.output.persistence.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "tipo_documento")
@Getter
@Setter

public class TipoDocumentoEntity {
    @Id
    @Column(length = 8)
    private String tipodoc_id;

    @Column(nullable = false, length = 50, unique = true)
    @NotBlank(message = "El nombre del tipo de documento no puede ser vacío")
    private String tipodoc_nombre;

    @Column(nullable = false)
    private boolean tipodoc_registro_vigente;


}
