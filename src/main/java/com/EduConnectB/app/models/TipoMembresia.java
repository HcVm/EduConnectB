package com.EduConnectB.app.models;

public enum TipoMembresia {
    ESTUDIANTE_ESTANDAR(TipoUsuario.ESTUDIANTE),
    ESTUDIANTE_PRO(TipoUsuario.ESTUDIANTE);

    private final TipoUsuario tipoUsuarioAsociado;

    TipoMembresia(TipoUsuario tipoUsuarioAsociado) {
        this.tipoUsuarioAsociado = tipoUsuarioAsociado;
    }

    public TipoUsuario getTipoUsuarioAsociado() {
        return tipoUsuarioAsociado;
    }
}