package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.ValoracionRepository;
import com.EduConnectB.app.models.Valoracion;

import java.util.List;
import java.util.Optional;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

    public List<Valoracion> obtenerTodasLasValoraciones() {
        return valoracionRepository.findAll();
    }

    public Optional<Valoracion> obtenerValoracionPorId(Integer idValoracion) {
        return valoracionRepository.findById(idValoracion);
    }

    public Double calcularPromedioValoracionesPorAsesor(Integer idAsesor) {
        return valoracionRepository.calcularPromedioValoracionesPorAsesor(idAsesor);
    }

    public Valoracion guardarValoracion(Valoracion valoracion) {
        return valoracionRepository.save(valoracion);
    }

    public void eliminarValoracion(Integer idValoracion) {
        valoracionRepository.deleteById(idValoracion);
    }
    
    public List<Valoracion> obtenerValoracionesPorAsesor(Integer idAsesor) {
        return valoracionRepository.findBySesionAsesorIdAsesor(idAsesor);
    }
}
