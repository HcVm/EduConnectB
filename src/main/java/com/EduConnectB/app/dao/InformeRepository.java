package com.EduConnectB.app.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Informe;

@Repository
public interface InformeRepository extends JpaRepository<Informe, Integer> {
	
	boolean existsByEstudianteIdUsuario(Integer idUsuario);
	
	@Query("SELECT i FROM Informe i WHERE i.estudiante.idUsuario = :idEstudiante")
    List<Informe> findInformesByEstudiante(@Param("idEstudiante") Integer idEstudiante);

}
