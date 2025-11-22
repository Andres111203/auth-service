package co.project.authservice.repository;

import co.project.authservice.entity.PerfilPorUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerfilPorUsuarioRepository extends JpaRepository<PerfilPorUsuario, Long> {

    @Query("SELECT ppu FROM PerfilPorUsuario ppu " +
            "JOIN FETCH ppu.perfil p " +
            "WHERE ppu.usuario.id = :usuarioId " +
            "AND ppu.registroVigente = true " +
            "AND p.registroVigente = true")
    List<PerfilPorUsuario> findActiveProfilesByUsuarioId(@Param("usuarioId") Long usuarioId);

    boolean existsByUsuarioIdAndPerfilIdAndRegistroVigenteTrue(Long usuarioId, Long perfilId);
}