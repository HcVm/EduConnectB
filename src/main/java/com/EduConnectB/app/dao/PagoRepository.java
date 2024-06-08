package com.EduConnectB.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

}
