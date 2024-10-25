package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class TipoMicromovilidad implements Serializable {

    private int idTipoMicromovilidad;
    private String tipo;

    public int getIdTipoMicromovilidad() {
        return idTipoMicromovilidad;
    }

    public void setIdTipoMicromovilidad(int idTipoMicromovilidad) {
        this.idTipoMicromovilidad = idTipoMicromovilidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
