package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.AsesorRepository;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Usuario;

import java.util.List;
import java.util.Optional;

@Service
public class AsesorService {

    @Autowired
    private AsesorRepository asesorRepository;

    public List<Asesor> obtenerTodosLosAsesores() {
        return asesorRepository.findAll();
    }

    public Optional<Asesor> obtenerAsesorPorId(Integer idAsesor) {
        return asesorRepository.findById(idAsesor);
    }

    public List<Asesor> obtenerAsesoresPorEspecialidad(String especialidad) {
        return asesorRepository.findByEspecialidad(especialidad);
    }

    public Asesor guardarAsesor(Asesor asesor) {
        return asesorRepository.save(asesor);
    }

    public void eliminarAsesor(Integer idAsesor) {
        asesorRepository.deleteById(idAsesor);
    }
    
    public Optional<Asesor> obtenerAsesorPorUsuario(Usuario usuario) {
        return asesorRepository.findByUsuario(usuario);
    }
    
    public Asesor actualizarAsesor(Integer idAsesor, Asesor asesorActualizado) {
        return asesorRepository.findById(idAsesor)
                .map(asesorExistente -> {
                    asesorExistente.setEspecialidad(asesorActualizado.getEspecialidad());
                    return asesorRepository.save(asesorExistente);
                })
                .orElseThrow(() -> new RuntimeException("Asesor no encontrado"));
    }
}
