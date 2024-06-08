package com.EduConnectB.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Calificacion;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
	 @Query("SELECT AVG(c.calificacion) FROM Calificacion c WHERE c.usuario.idUsuario = :idUsuario AND c.nombreMateria = :nombreMateria")
	 Double calcularPromedioCalificacionesPorMateria(Integer idUsuario, String nombreMateria);
	 
	 List<Calificacion> findByUsuarioIdUsuario(Integer idUsuario); 
	 
	 List<Calificacion> findByUsuarioIdUsuarioAndNombreMateria(Integer idUsuario, String nombreMateria);

}
