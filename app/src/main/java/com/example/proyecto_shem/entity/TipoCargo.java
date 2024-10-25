package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class TipoCargo implements Serializable {

    private int idTipoCargo;
    private String tipo;

    public int getIdTipoCargo() {
        return idTipoCargo;
    }

    public void setIdTipoCargo(int idTipoCargo) {
        this.idTipoCargo = idTipoCargo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
