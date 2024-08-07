package com.EduConnectB.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EduConnectB.app.dao.UsuarioRepository;
import com.EduConnectB.app.models.TipoUsuario;
import com.EduConnectB.app.models.Usuario;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }
    
    public Usuario buscarPorCorreoElectronico(String correoElectronico) {
        return usuarioRepository.findByCorreoElectronico(correoElectronico);
    }

    public List<Usuario> buscarPorTipoUsuario(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }
    
    public Usuario actualizarUsuario(Integer idUsuario, Usuario usuarioActualizado) {
        return usuarioRepository.findById(idUsuario)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuarioActualizado.getNombre());
                    usuarioExistente.setEstado(usuarioActualizado.getEstado());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return usuarioRepository.existsByCorreoElectronico(correoElectronico);
    }
    
    public Optional<Usuario> findByTokenTemporal(String tokenTemporal) {
        return usuarioRepository.findByTokenTemporal(tokenTemporal);
    }
    
    @Transactional
    public String generarTokenRestablecimiento(Usuario usuario) {
    	usuario.setTokenRestablecimiento(null); 
        usuarioRepository.save(usuario);
        String token = UUID.randomUUID().toString();
        usuario.setTokenRestablecimiento(token);
        usuarioRepository.save(usuario);
        return token;
    }
    
    public Usuario findByTokenRestablecimiento(String token) {
        return usuarioRepository.findByTokenRestablecimiento(token);
    }
}
