package com.EduConnectB.app.dao;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Usuario;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {
	List<Membresia> findByFechaFinBefore(LocalDate fecha);
	
	Membresia findByUsuario(Usuario usuario);

}
