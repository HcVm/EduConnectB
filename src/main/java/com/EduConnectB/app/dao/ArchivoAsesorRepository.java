package com.EduConnectB.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.ArchivoAsesor;

@Repository
public interface ArchivoAsesorRepository extends JpaRepository<ArchivoAsesor, Integer> {

}
