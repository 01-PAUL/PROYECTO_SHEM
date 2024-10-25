package com.example.proyecto_shem.entity;

import android.os.Build;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class Permiso implements Serializable {

    private int idPermiso;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String genero;
    private String nacionalidad;
    private String imgUsuario;
    private String detallePermiso;
    private Date fechaSalida;
    private String horaSalida;
    private TipoMicromovilidad tipoMicromovilidad;

    // Constructor vacío
    public Permiso() {
        this.fechaSalida = new Date();  // Asigna la fecha actual

        // Verifica si la versión de Android es al menos API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.horaSalida = LocalTime.now().toString();  // Asigna la hora local si el SDK es >= 26
        } else {
            this.horaSalida = new Date().toString();  // Usa una alternativa para versiones anteriores
        }
    }

    // Getters y setters

    public int getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public String getDetallePermiso() {
        return detallePermiso;
    }

    public void setDetallePermiso(String detallePermiso) {
        this.detallePermiso = detallePermiso;
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
