package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.dao.MembresiaRepository;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {

    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Membresia> obtenerTodasLasMembresias() {
        return membresiaRepository.findAll();
    }

    public Optional<Membresia> obtenerMembresiaPorId(Integer idMembresia) {
        return membresiaRepository.findById(idMembresia);
    }

    public List<Membresia> obtenerMembresiasPorVencerAntesDe(LocalDate fecha) {
        return membresiaRepository.findByFechaFinBefore(fecha);
    }

    public Membresia guardarMembresia(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void eliminarMembresia(Integer idMembresia) {
        membresiaRepository.deleteById(idMembresia);
    }
    
    public boolean tieneMembresiaActiva(Usuario usuario) {
        Membresia membresia = membresiaRepository.findByUsuario(usuario);
        if (membresia != null) {
            LocalDate fechaActual = LocalDate.now();
            return membresia.getFechaFin() != null && membresia.getFechaFin().isAfter(fechaActual);
        }
        return false;
    }
    
    //acá simulamos un pago de renovación, como en la adquisición de la membresía
    @Transactional
    public Membresia renovarMembresia(Usuario usuario, boolean esPrueba) {
        Membresia membresia = membresiaRepository.findByUsuario(usuario);
        if (membresia != null) {
            LocalDate nuevaFechaFin = esPrueba ? LocalDate.now().plusMonths(1) : membresia.getFechaFin().plusDays(30);
            membresia.setFechaFin(nuevaFechaFin);
            return membresiaRepository.save(membresia);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una membresía activa para renovar.");
        }
    }


    @Transactional
    public void cancelarMembresia(Usuario usuario) {
        Membresia membresia = membresiaRepository.findByUsuario(usuario);
        if (membresia != null) {
            membresia.setFechaFin(LocalDate.now());
            membresiaRepository.save(membresia);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes una membresía activa para cancelar.");
        }
    }
    
    public Membresia obtenerMembresiaPorUsuario(Usuario usuario) {
        return membresiaRepository.findByUsuario(usuario);
    }
}
