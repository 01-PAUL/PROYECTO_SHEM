package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class TipoUsuario implements Serializable {

    private int idTipoUsuario;
    private String tipo;

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
