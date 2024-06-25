package com.EduConnectB.app.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Informe;

@Repository
public interface InformeRepository extends JpaRepository<Informe, Integer> {
	
	boolean existsByEstudianteIdUsuario(Integer idUsuario);

}
