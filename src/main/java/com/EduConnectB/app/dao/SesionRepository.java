package com.EduConnectB.app.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Sesion;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Integer> {
	Optional<Sesion> findById(Integer idSesion);
	List<Sesion> findByUsuarioIdUsuario(Integer idUsuario);
    List<Sesion> findByAsesorIdAsesor(Integer idAsesor);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Sesion s " +
            "WHERE s.asesor.idAsesor = :idAsesor " +
            "AND s.usuario.idUsuario = :idEstudiante " +
            "AND s.fechaHora BETWEEN :inicioMes AND :finMes")
     boolean existsByAsesorIdAsesorAndUsuarioIdUsuarioAndFechaHoraBetween(
             @Param("idAsesor") Integer idAsesor, 
             @Param("idEstudiante") Integer idEstudiante, 
             @Param("inicioMes") LocalDate inicioMes, 
             @Param("finMes") LocalDate finMes);
}
