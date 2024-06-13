package com.EduConnectB.app.services;

import com.EduConnectB.app.models.Asesor;

import org.springframework.stereotype.Service;

@Service
public class NotificacionService {


    public void enviarNotificacionNuevoAsesor(Asesor asesor) {
    	System.out.println("Nuevo asesor registrado: " + asesor.getUsuario().getNombre());
    }
}
