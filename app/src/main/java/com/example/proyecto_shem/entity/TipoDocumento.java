package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class TipoDocumento implements Serializable {

    private int idTipoDocumento;
    private String tipo;

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
