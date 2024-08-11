package com.EduConnectB.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.AsesorRepository;
import com.EduConnectB.app.dao.MembresiaRepository;
import com.EduConnectB.app.dao.PagoRepository;
import com.EduConnectB.app.dao.ValoracionRepository;
import com.EduConnectB.app.dto.MembresiaDTO;
import com.EduConnectB.app.dto.MembresiasResponse;
import com.EduConnectB.app.models.Asesor;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Pago;
import com.EduConnectB.app.models.Valoracion;


@Service
public class ReportesService {
	
	@Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;
    
    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private AsesorRepository asesorRepository;

    public BigDecimal calcularGananciasTotales(LocalDateTime inicio, LocalDateTime fin) {
        return pagoRepository.sumarMontoPorPeriodo(inicio, fin);
    }

    public List<Pago> obtenerPagosPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return pagoRepository.findByFechaBetween(inicio, fin);
    }

    public Long contarMembresiasVendidas(LocalDate inicio, LocalDate fin) {
        return membresiaRepository.contarPorPeriodo(inicio, fin);
    }

    public List<MembresiaDTO> obtenerMembresiasPorPeriodo(LocalDate inicio, LocalDate fin) {
        List<Membresia> membresias = membresiaRepository.findByFechaInicioBetween(inicio, fin);
        return membresias.stream()
                         .map(membresia -> new MembresiaDTO(
                             membresia.getIdMembresia(),
                             membresia.getTipoMembresia(),
                             membresia.getFechaInicio(),
                             membresia.getFechaFin()))
                         .collect(Collectors.toList());
    }

    public List<Valoracion> obtenerValoracionesPorAsesor(Integer idAsesor) {
        return valoracionRepository.findBySesionAsesorIdAsesor(idAsesor);
    }

    public Double calcularPromedioPuntuaciones(Integer idAsesor) {
        List<Valoracion> valoraciones = obtenerValoracionesPorAsesor(idAsesor);
        return valoraciones.stream()
                           .mapToInt(Valoracion::getPuntuacion)
                           .average()
                           .orElse(0.0);
    }

    public Map<Asesor, Double> obtenerRendimientoAsesores() {
        List<Asesor> asesores = asesorRepository.findAll();
        Map<Asesor, Double> rendimientoMap = new HashMap<>();

        for (Asesor asesor : asesores) {
            Double promedioPuntuacion = calcularPromedioPuntuaciones(asesor.getIdAsesor());
            rendimientoMap.put(asesor, promedioPuntuacion);
        }

        return rendimientoMap;
    }
}
