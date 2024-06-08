package com.EduConnectB.app.services;

import org.springframework.stereotype.Service;

@Service
public class JitsiService {

    private static final String JITSI_SERVER_URL = "https://meet.jit.si"; // O la URL de tu servidor Jitsi

    public String generarUrlSala(Integer idSesion) {
        String nombreSala = "educonnect-sesion-" + idSesion; // Genera un nombre único para la sala
        return JITSI_SERVER_URL + "/" + nombreSala;
    }
}
