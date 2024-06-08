package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.MensajeRepository;
import com.EduConnectB.app.models.Mensaje;

import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    public List<Mensaje> obtenerTodosLosMensajes() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> obtenerMensajePorId(Integer idMensaje) {
        return mensajeRepository.findById(idMensaje);
    }

    public Mensaje guardarMensaje(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public void eliminarMensaje(Integer idMensaje) {
        mensajeRepository.deleteById(idMensaje);
    }
}
