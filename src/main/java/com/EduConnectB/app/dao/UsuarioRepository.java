package com.EduConnectB.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByCorreoElectronico(String correoElectronico);

    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario = :tipoUsuario")
    List<Usuario> findByTipoUsuario(@Param("tipoUsuario") TipoUsuario tipoUsuario);
    
    boolean existsByCorreoElectronico(String correoElectronico); 
    
    Optional<Usuario> findByTokenTemporal(String tokenTemporal);
    
    Usuario findByTokenRestablecimiento(String token);
    
}
