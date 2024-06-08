package com.EduConnectB.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	Usuario findByCorreoElectronico(String correoElectronico);

    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario = :tipoUsuario")
    List<Usuario> findByTipoUsuario(@Param("tipoUsuario") TipoUsuario tipoUsuario);
    
    boolean existsByCorreoElectronico(String correoElectronico); 
}
