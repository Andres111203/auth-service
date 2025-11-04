package co.project.authservice.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(name = "usuario")
@Getter
@Setter

public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usu_id;

    @OneToOne
    @JoinColumn(name = "usu_per_id", referencedColumnName = "per_id", nullable = true)
    private PersonaEntity persona;

    @Column(length = 100, unique = true)
    private String usu_login;

    @Column(length = 255)
    @NotBlank(message = "La contraseña del usuario no puede estar vacía")
    private String usu_password;

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank(message = "El email del usuario no puede estar vacío")
    private String usu_email;

    @Column(length = 50)
    private String usu_oauth_provider;

    @Column(length = 255)
    private String usu_oauth_provider_id;

    @Column(columnDefinition = "TEXT")
    private String usu_oauth_access_token;

    @Column(columnDefinition = "TEXT")
    private String usu_oauth_refresh_token;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime usu_fecha_vencimiento;

    private Integer usu_vigencia_password;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime usu_fecha_modif_password;

    @Column(nullable = false)
    private Boolean usu_registro_vigente = true;

    @Column(length = 500)
    private String usu_profile_picture_url;

    @Column(nullable = false)
    private Boolean usu_email_verificado = false;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private LocalDateTime usu_fecha_registro;



}
