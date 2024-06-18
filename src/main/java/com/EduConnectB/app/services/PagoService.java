package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.dao.PagoRepository;
import com.EduConnectB.app.dto.DatosPago;
import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.Pago;
import com.EduConnectB.app.models.TipoMembresia;
import com.EduConnectB.app.models.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> obtenerPagoPorId(Integer idPago) {
        return pagoRepository.findById(idPago);
    }

    public Pago guardarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void eliminarPago(Integer idPago) {
    	pagoRepository.deleteById(idPago);
    }
    
    public Pago obtenerUltimoPago(Membresia membresia) {
        return pagoRepository.findFirstByMembresiaOrderByFechaDesc(membresia);
    }
    
    public Pago procesarPago(Usuario usuario, TipoMembresia tipoMembresia, DatosPago datosPago) {
        if (!validarDatosTarjeta(datosPago)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de tarjeta inválidos.");
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMonto(calcularMontoMembresia(tipoMembresia));
        pago.setFecha(LocalDateTime.now());
        return pagoRepository.save(pago);
    }

    private boolean validarDatosTarjeta(DatosPago datosPago) {
        return datosPago.getNumeroTarjeta().length() == 16 &&
               datosPago.getCvv().length() == 3;
    }

    private BigDecimal calcularMontoMembresia(TipoMembresia tipoMembresia) {
        switch (tipoMembresia) {
            case ESTUDIANTE_ESTANDAR:
                return new BigDecimal("100.00");
            case ESTUDIANTE_PRO:
                return new BigDecimal("180.00");
            default:
                throw new IllegalArgumentException("Tipo de membresía no válido");
        }
    }
}
