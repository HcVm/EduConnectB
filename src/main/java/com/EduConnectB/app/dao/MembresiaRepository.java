package com.EduConnectB.app.dao;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Usuario;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {
	List<Membresia> findByFechaFinBefore(LocalDate fecha);
	
	Membresia findByUsuario(Usuario usuario);
	
	List<Membresia> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
	
	@Query("SELECT COUNT(m) FROM Membresia m WHERE m.fechaInicio BETWEEN :inicio AND :fin")
    Long contarPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

}
