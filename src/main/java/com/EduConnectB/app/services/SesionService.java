package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.EduConnectB.app.dao.NotificacionRepository;
import com.EduConnectB.app.dao.SesionRepository;
import com.EduConnectB.app.models.EstadoSesion;
import com.EduConnectB.app.models.Notificacion;
import com.EduConnectB.app.models.Sesion;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SesionService {

    @Autowired
    private SesionRepository sesionRepository;
    
    @Autowired
    private JitsiService jitsiService;
    
    @Autowired
    private MembresiaService membresiaService;
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    
    public List<Sesion> obtenerTodasLasSesiones() {
        return sesionRepository.findAll();
    }
    
    public Optional<Sesion> obtenerSesionPorId(Integer idSesion) {
        return sesionRepository.findById(idSesion);
    }

    public List<Sesion> obtenerSesionesPorUsuario(Integer idUsuario) {
        return sesionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public List<Sesion> obtenerSesionesPorAsesor(Integer idAsesor) {
        return sesionRepository.findByAsesorIdAsesor(idAsesor);
    }

    @Transactional
    public Sesion guardarSesion(Sesion sesion) {
        if (sesion.getUsuario() != null && !membresiaService.tieneMembresiaActiva(sesion.getUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El estudiante no tiene una membresía válida para programar sesiones.");
        }
        Sesion sesionGuardada = sesionRepository.save(sesion);
        
        String mensaje = "Tienes una nueva sesión pendiente de aprobación.";

        Notificacion notificacionAsesor = new Notificacion();
        notificacionAsesor.setUsuario(sesionGuardada.getAsesor().getUsuario());
        notificacionAsesor.setMensaje(mensaje);
        notificacionAsesor.setFechaHora(LocalDateTime.now());
        notificacionAsesor.setLeido(false);
        notificacionRepository.save(notificacionAsesor);
        
        Map<String, Object> notificacionData = new HashMap<>();
        notificacionData.put("id", notificacionAsesor.getIdNotificacion());
        notificacionData.put("message", mensaje);

        messagingTemplate.convertAndSend("/topic/notificaciones/" + sesionGuardada.getAsesor().getUsuario().getIdUsuario(), notificacionData);
        
        programarRecordatorio(sesionGuardada);
        
        return sesionGuardada;
        
    }
    
	@Transactional
    public void aceptarSolicitudSesion(Sesion sesion) {
        sesion.setEstado(EstadoSesion.PROGRAMADA);
        sesion.setUrlJitsi(jitsiService.generarUrlSala(sesion.getIdSesion()));
        sesionRepository.save(sesion);
        
        String mensaje = "Tu sesión ha sido aceptada y programada para la fecha y hora solicitadas.";
        
        Notificacion notificacionUsuario = new Notificacion();
        notificacionUsuario.setUsuario(sesion.getUsuario());
        notificacionUsuario.setMensaje(mensaje);
        notificacionUsuario.setFechaHora(LocalDateTime.now());
        notificacionUsuario.setLeido(false);
        notificacionRepository.save(notificacionUsuario);
        
        Map<String, Object> notificacionData = new HashMap<>();
        notificacionData.put("id", notificacionUsuario.getIdNotificacion());
        notificacionData.put("message", mensaje);
        
        Map<String, Object> sesionData = new HashMap<>();
        sesionData.put("idSesion", sesion.getIdSesion());
        sesionData.put("fechaHora", sesion.getFechaHora());
        sesionData.put("estado", sesion.getEstado().toString());
        sesionData.put("urlJitsi", sesion.getUrlJitsi());
        
        Map<String, Object> asesorData = new HashMap<>();
        asesorData.put("usuario", sesion.getAsesor().getUsuario());
        sesionData.put("asesor", asesorData);

        System.out.println("Datos de sesión enviados: " + sesionData);


        messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getUsuario().getIdUsuario(), notificacionData);
        messagingTemplate.convertAndSend("/topic/sesiones/" + sesion.getUsuario().getIdUsuario(), sesionData);
    }

    @Transactional
    public void rechazarSolicitudSesion(Sesion sesion) {
        sesion.setEstado(EstadoSesion.RECHAZADA);
        sesionRepository.save(sesion);
        
        String mensaje = "Tu solicitud de sesión ha sido rechazada. Por favor, busca otro asesor o selecciona otro horario.";
        
        Notificacion notificacionUsuario = new Notificacion();
        notificacionUsuario.setUsuario(sesion.getUsuario());
        notificacionUsuario.setMensaje(mensaje);
        notificacionUsuario.setFechaHora(LocalDateTime.now());
        notificacionUsuario.setLeido(false);
        notificacionRepository.save(notificacionUsuario);
        
        Map<String, Object> notificacionData = new HashMap<>();
        notificacionData.put("id", notificacionUsuario.getIdNotificacion());
        notificacionData.put("message", mensaje);

        messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getUsuario().getIdUsuario(), notificacionData);
    }
    
    @Transactional
    public void finalizarSesion(Integer idSesion, Usuario usuarioAutenticado) {
        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (sesion.getUsuario().equals(usuarioAutenticado) || 
            sesion.getAsesor().getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
            sesion.setEstado(EstadoSesion.FINALIZADA);
            sesionRepository.save(sesion);
        }
    }
    
    @Transactional
    public void cancelarSesion(Integer idSesion, Usuario usuarioAutenticado) {
        Sesion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (sesion.getUsuario().equals(usuarioAutenticado) || 
            sesion.getAsesor().getUsuario().getIdUsuario().equals(usuarioAutenticado.getIdUsuario())) {
            sesion.setEstado(EstadoSesion.CANCELADA);
            sesionRepository.save(sesion);
            
            String mensaje = "La sesión con ID " + idSesion + " ha sido cancelada.";

            Notificacion notificacionUsuario = new Notificacion();
            notificacionUsuario.setUsuario(sesion.getUsuario());
            notificacionUsuario.setMensaje(mensaje);
            notificacionUsuario.setFechaHora(LocalDateTime.now());
            notificacionUsuario.setLeido(false);
            notificacionRepository.save(notificacionUsuario);

            Notificacion notificacionAsesor = new Notificacion();
            notificacionAsesor.setUsuario(sesion.getAsesor().getUsuario());
            notificacionAsesor.setMensaje(mensaje);
            notificacionAsesor.setFechaHora(LocalDateTime.now());
            notificacionAsesor.setLeido(false);
            notificacionRepository.save(notificacionAsesor);
            
            Map<String, Object> notificacionData1 = new HashMap<>();
            notificacionData1.put("id", notificacionUsuario.getIdNotificacion());
            notificacionData1.put("message", mensaje);
            
            Map<String, Object> notificacionData = new HashMap<>();
            notificacionData.put("id", notificacionAsesor.getIdNotificacion());
            notificacionData.put("message", mensaje);
            
            Map<String, Object> sesionData = new HashMap<>();
            sesionData.put("idSesion", sesion.getIdSesion());
            sesionData.put("fechaHora", sesion.getFechaHora());
            sesionData.put("estado", sesion.getEstado().toString());
            sesionData.put("urlJitsi", sesion.getUrlJitsi());
            
            Map<String, Object> asesorData = new HashMap<>();
            asesorData.put("usuario", sesion.getAsesor().getUsuario());
            sesionData.put("asesor", asesorData);

            System.out.println("Datos de sesión enviados: " + sesionData);

            messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getUsuario().getIdUsuario(), notificacionData1);
            messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getAsesor().getUsuario().getIdUsuario(), notificacionData);
            messagingTemplate.convertAndSend("/topic/sesiones/" + sesion.getUsuario().getIdUsuario(), sesionData);
            messagingTemplate.convertAndSend("/topic/sesiones/" + sesion.getAsesor().getUsuario().getIdUsuario(), sesionData);
        }
    }
   
    
    public boolean asesorTuvoSesionesConEstudianteEnPeriodo(Integer idAsesor, Integer idEstudiante, YearMonth mesAnio) {
        LocalDate inicioMes = mesAnio.atDay(1);
        LocalDate finMes = mesAnio.atEndOfMonth();
        return sesionRepository.existsByAsesorIdAsesorAndUsuarioIdUsuarioAndFechaHoraBetween(idAsesor, idEstudiante, inicioMes, finMes);
    }
    
    private void programarRecordatorio(Sesion sesion) {
        LocalDateTime fechaHoraSesion = sesion.getFechaHora();
        LocalDateTime ahora = LocalDateTime.now();
        long minutosAntesSesion = Duration.between(ahora, fechaHoraSesion).toMinutes() - 30;

        if (minutosAntesSesion > 0) {
            scheduler.schedule(() -> enviarRecordatorio(sesion), minutosAntesSesion, TimeUnit.MINUTES);
        }
    }
    
    private void enviarRecordatorio(Sesion sesion) {
        String mensaje = "Recordatorio: Tienes una sesión programada en 30 minutos.";

        Notificacion notificacionUsuario = new Notificacion();
        notificacionUsuario.setUsuario(sesion.getUsuario());
        notificacionUsuario.setMensaje(mensaje);
        notificacionUsuario.setFechaHora(LocalDateTime.now());
        notificacionUsuario.setLeido(false);
        notificacionRepository.save(notificacionUsuario);

        Map<String, Object> notificacionData = new HashMap<>();
        notificacionData.put("id", notificacionUsuario.getIdNotificacion());
        notificacionData.put("message", mensaje);

        messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getUsuario().getIdUsuario(), notificacionData);

        Notificacion notificacionAsesor = new Notificacion();
        notificacionAsesor.setUsuario(sesion.getAsesor().getUsuario());
        notificacionAsesor.setMensaje(mensaje);
        notificacionAsesor.setFechaHora(LocalDateTime.now());
        notificacionAsesor.setLeido(false);
        notificacionRepository.save(notificacionAsesor);

        Map<String, Object> notificacionData1 = new HashMap<>();
        notificacionData1.put("id", notificacionAsesor.getIdNotificacion());
        notificacionData1.put("message", mensaje);

        messagingTemplate.convertAndSend("/topic/notificaciones/" + sesion.getAsesor().getUsuario().getIdUsuario(), notificacionData1);
    }
}
