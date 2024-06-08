package com.EduConnectB.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
	
}
