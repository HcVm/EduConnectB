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

import com.EduConnectB.app.dao.MembresiaRepository;
import com.EduConnectB.app.dao.PagoRepository;
import com.EduConnectB.app.dao.ValoracionRepository;
import com.EduConnectB.app.dto.AsesorDTO;
import com.EduConnectB.app.dto.MembresiaDTO;
import com.EduConnectB.app.dto.SesionDTO;
import com.EduConnectB.app.dto.ValoracionDTO;
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

    public Map<String, Map<String, Object>> obtenerRendimientoAsesoresPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<Object[]> resultados = valoracionRepository.findRendimientoAsesoresByPeriodo(inicio, fin);
        Map<String, Map<String, Object>> rendimientoMap = new HashMap<>();

        for (Object[] resultado : resultados) {
            String asesorNombre = (String) resultado[0];
            String materia = (String) resultado[1];
            Long cantidadSesiones = (Long) resultado[2];
            Double promedioPuntuacion = (Double) resultado[3];

            Map<String, Object> detallesAsesor = rendimientoMap.computeIfAbsent(asesorNombre, k -> new HashMap<>());
            detallesAsesor.put("materia", materia);
            detallesAsesor.put("cantidadSesiones", cantidadSesiones);
            detallesAsesor.put("promedioPuntuacion", promedioPuntuacion);

            List<Valoracion> valoracionesAsesor = valoracionRepository.findBySesionAsesorUsuarioNombreAndSesionFechaHoraBetween(
                    asesorNombre, inicio, fin);

            List<SesionDTO> sesionesDTO = valoracionesAsesor.stream()
                    .map(valoracion -> {
                        SesionDTO sesionDTO = new SesionDTO();
                        sesionDTO.setIdSesion(valoracion.getSesion().getIdSesion());
                        sesionDTO.setFechaHora(valoracion.getSesion().getFechaHora());

                        AsesorDTO asesorDTO = new AsesorDTO();
                        asesorDTO.setIdAsesor(valoracion.getSesion().getAsesor().getIdAsesor());
                        asesorDTO.setNombre(valoracion.getSesion().getAsesor().getUsuario().getNombre());
                        asesorDTO.setEspecialidad(valoracion.getSesion().getAsesor().getEspecialidad());
                        sesionDTO.setAsesor(asesorDTO);

                        if (valoracion != null) {
                            ValoracionDTO valoracionDTO = new ValoracionDTO();
                            valoracionDTO.setIdValoracion(valoracion.getIdValoracion());
                            valoracionDTO.setPuntuacion(valoracion.getPuntuacion());
                            valoracionDTO.setComentario(valoracion.getComentario());
                            sesionDTO.setValoracion(valoracionDTO);
                        }

                        return sesionDTO;
                    })
                    .collect(Collectors.toList());

            detallesAsesor.put("sesiones", sesionesDTO); 
        }

        return rendimientoMap;
    }

}