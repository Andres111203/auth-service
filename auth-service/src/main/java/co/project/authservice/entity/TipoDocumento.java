package co.project.authservice.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoDocumento {

    @Id
    @Column(name = "tipodoc_id", length = 8)
    private String id;

    @Column(name = "tipodoc_nombre", length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(name = "tipodoc_registro_vigente")
    private Boolean registroVigente = true;
}