package com.EduConnectB.app.config;

import com.EduConnectB.app.models.EstadoUsuario;
import com.EduConnectB.app.models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class EduConnectUserDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
    private final Usuario usuario;

    public EduConnectUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(usuario.getTipoUsuario().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreoElectronico();
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstado() == EstadoUsuario.ACTIVO;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
