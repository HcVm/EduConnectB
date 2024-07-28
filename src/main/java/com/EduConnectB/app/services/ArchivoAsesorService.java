package com.EduConnectB.app.services;

import com.EduConnectB.app.dao.ArchivoAsesorRepository;
import com.EduConnectB.app.models.ArchivoAsesor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class ArchivoAsesorService {

    @Autowired
    private ArchivoAsesorRepository archivoAsesorRepository;

    public byte[] descargarArchivo(Integer archivoId) throws IOException {
        ArchivoAsesor archivo = archivoAsesorRepository.findById(archivoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado"));

        URL url = new URL(archivo.getRutaArchivo()); 
        InputStream inputStream = url.openStream();
        byte[] rawBytes = inputStream.readAllBytes();

        return rawBytes;
    }
}

