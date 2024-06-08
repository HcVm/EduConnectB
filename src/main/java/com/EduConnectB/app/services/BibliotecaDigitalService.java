package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.EduConnectB.app.models.Membresia;
import com.EduConnectB.app.models.RecursoBiblioteca;
import com.EduConnectB.app.models.TipoMembresia;
import com.EduConnectB.app.models.Usuario;

@Service
public class BibliotecaDigitalService {
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
    private MembresiaService membresiaService;
	
	public boolean tieneAccesoABiblioteca(Usuario usuario) {
        Membresia membresia = membresiaService.obtenerMembresiaPorUsuario(usuario);

        return membresia != null && membresia.getTipoMembresia() == TipoMembresia.ESTUDIANTE_PRO;
    }
	
	public RecursoBiblioteca obtenerRecurso(String idRecurso) {
        String apiUrl = "http://api-biblioteca-digital/recursos/" + idRecurso; //(recordatorio) Reemplaza con la URL real
        ResponseEntity<RecursoBiblioteca> response = restTemplate.getForEntity(apiUrl, RecursoBiblioteca.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al obtener el recurso de la biblioteca digital.");
        }
    }

}
