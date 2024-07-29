package com.EduConnectB.app.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EduConnectB.app.models.Notificacion;
import com.EduConnectB.app.models.Usuario;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByUsuario(Usuario usuario);
    Optional<Notificacion> findByIdNotificacionAndUsuario(Integer idNotificacion, Usuario usuario);

}