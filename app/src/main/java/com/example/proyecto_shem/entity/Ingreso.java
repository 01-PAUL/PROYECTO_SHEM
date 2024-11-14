package com.example.proyecto_shem.entity;

import android.os.Build;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class Ingreso implements Serializable {

    private int idIngreso;
    private String tipoUsuario;
    private String usuario;
    private String tipoDocumento;
    private String numeroDocumento;
    private String codigoUsuario;
    private String imageUrl;
    private String fechaIngreso;
    private String horaIngreso;
    private String tipoMicromovilidad;


    public int getIdIngreso() {
        return idIngreso;
    }


    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public String getTipoMicromovilidad() {
        return tipoMicromovilidad;
    }

    public void setTipoMicromovilidad(String tipoMicromovilidad) {
        this.tipoMicromovilidad = tipoMicromovilidad;
    }
}