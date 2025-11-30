package co.project.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usu_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_per_id")
    private Persona persona;

    @Column(name = "usu_login", length = 100, unique = true)
    private String login;

    @Column(name = "usu_password", length = 255)
    private String password;

    @Column(name = "usu_email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "usu_oauth_provider", length = 50)
    private String oauthProvider;

    @Column(name = "usu_oauth_provider_id", length = 255)
    private String oauthProviderId;

    @Column(name = "usu_oauth_access_token", columnDefinition = "TEXT")
    private String oauthAccessToken;

    @Column(name = "usu_oauth_refresh_token", columnDefinition = "TEXT")
    private String oauthRefreshToken;

    @Column(name = "usu_fecha_vencimiento")
    private ZonedDateTime fechaVencimiento;

    @Column(name = "usu_vigencia_password")
    private Integer vigenciaPassword;

    @Column(name = "usu_fecha_modif_password")
    private ZonedDateTime fechaModifPassword;

    @Column(name = "usu_registro_vigente")
    private Boolean registroVigente = true;

    @Column(name = "usu_profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "usu_email_verificado")
    private Boolean emailVerificado = false;

    @Column(name = "usu_fecha_registro", nullable = false)
    private ZonedDateTime fechaRegistro = ZonedDateTime.now();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<PerfilPorUsuario> perfiles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = ZonedDateTime.now();
        }
    }
}