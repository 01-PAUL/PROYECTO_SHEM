package com.example.proyecto_shem.entity;

import android.os.Build;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class Salida implements Serializable {

    private int idSalida;
    private TipoUsuario tipoUsuario;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String codUsuario;
    private String imgUsuario;
    private Date fechaSalida;
    private String horaSalida;
    private TipoMicromovilidad tipoMicromovilidad;

    // Constructor vacío
    public Salida() {
        this.fechaSalida = new Date();  // Asigna la fecha actual

        // Verifica si la versión de Android es al menos API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.horaSalida = LocalTime.now().toString();  // Asigna la hora local si el SDK es >= 26
        } else {
            this.horaSalida = new Date().toString();  // Usa una alternativa para versiones anteriores
        }
    }

    // Getters y setters
    public int getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(int idSalida) {
        this.idSalida = idSalida;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public TipoMicromovilidad getTipoMicromovilidad() {
        return tipoMicromovilidad;
    }

    public void setTipoMicromovilidad(TipoMicromovilidad tipoMicromovilidad) {
        this.tipoMicromovilidad = tipoMicromovilidad;
    }
}
