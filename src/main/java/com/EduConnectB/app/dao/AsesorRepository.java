package com.EduConnectB.app.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Usuario;

@Repository
public interface AsesorRepository extends JpaRepository<Asesor, Integer> {
	List<Asesor> findByEspecialidad(String especialidad);
	
	Optional<Asesor> findByUsuario(Usuario usuario);
	
	List<Asesor> findByUsuarioEstado(EstadoUsuario estado);
	
}
