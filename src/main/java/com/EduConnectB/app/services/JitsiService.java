package com.EduConnectB.app.services;

import org.springframework.stereotype.Service;

@Service
public class JitsiService {

    private static final String JITSI_SERVER_URL = "https://meet.jit.si";

    public String generarUrlSala(Integer idSesion) {
        String nombreSala = "educonnect-sesion-" + idSesion;
        return JITSI_SERVER_URL + "/" + nombreSala;
    }
}
