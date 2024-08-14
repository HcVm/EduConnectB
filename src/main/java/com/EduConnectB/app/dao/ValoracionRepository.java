package com.EduConnectB.app.dao;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {
	
	 @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.sesion.asesor.idAsesor = :idAsesor")
	 Double calcularPromedioValoracionesPorAsesor(Integer idAsesor);
	 
	 List<Valoracion> findBySesionAsesorIdAsesor(Integer idAsesor);
	 
	 @Query("SELECT v FROM Valoracion v " +
	           "JOIN v.sesion s " +
	           "JOIN s.asesor a " +
	           "WHERE a.usuario.nombre = :asesorNombre AND s.fechaHora BETWEEN :fechaInicio AND :fechaFin")
	    List<Valoracion> findBySesionAsesorUsuarioNombreAndSesionFechaHoraBetween(
	            @Param("asesorNombre") String asesorNombre,
	            @Param("fechaInicio") LocalDateTime fechaInicio,
	            @Param("fechaFin") LocalDateTime fechaFin);
	 
	 @Query("SELECT a.usuario.nombre AS asesor, a.especialidad AS materia, COUNT(DISTINCT ses) AS cantidadSesiones, COALESCE(AVG(v.puntuacion), 0) AS promedio " +
		       "FROM Asesor a " +
		       "LEFT JOIN a.sesiones ses ON ses.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
		       "LEFT JOIN ses.valoracion v " + 
		       "GROUP BY a.usuario.nombre, a.especialidad")
		List<Object[]> findRendimientoAsesoresByPeriodo(
		        @Param("fechaInicio") LocalDateTime fechaInicio, 
		        @Param("fechaFin") LocalDateTime fechaFin);

	 
}
