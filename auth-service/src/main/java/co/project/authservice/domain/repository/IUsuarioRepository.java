package co.project.authservice.domain.repository;

import co.project.authservice.domain.model.Usuario;

public interface IUsuarioRepository {
    Usuario findByEmail(String email);
    Usuario findByUsulogin(String usulogin);
    Usuario findByEmailAndUsulogin(String email, String usulogin);
    Usuario findByDocumento(String documento);
    Usuario updateUsuario(Usuario usuario);
    void deleteUsuario(Usuario usuario);
}
