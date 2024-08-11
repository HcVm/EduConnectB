package com.EduConnectB.app.dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {
	
	 @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.sesion.asesor.idAsesor = :idAsesor")
	 Double calcularPromedioValoracionesPorAsesor(Integer idAsesor);
	 
	 List<Valoracion> findBySesionAsesorIdAsesor(Integer idAsesor);
	 
}
