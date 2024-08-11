package com.EduConnectB.app.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
	
	Pago findFirstByMembresiaOrderByFechaDesc(Membresia membresia);
	
	@Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarMontoPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
	
	@Query("SELECT p FROM Pago p WHERE p.fecha BETWEEN :inicio AND :fin")
    List<Pago> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
