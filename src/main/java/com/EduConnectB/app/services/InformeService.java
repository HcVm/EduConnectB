package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.InformeRepository;
import com.EduConnectB.app.models.Informe;

import java.util.List;
import java.util.Optional;

@Service
public class InformeService {

    @Autowired
    private InformeRepository informeRepository;

    public List<Informe> obtenerTodosLosInformes() {
        return informeRepository.findAll();
    }

    public Optional<Informe> obtenerInformePorId(Integer idInforme) {
        return informeRepository.findById(idInforme);
    }

    public Informe guardarInforme(Informe informe) {
        return informeRepository.save(informe);
    }

    public void eliminarInforme(Integer idInforme) {
        informeRepository.deleteById(idInforme);
    }
}
