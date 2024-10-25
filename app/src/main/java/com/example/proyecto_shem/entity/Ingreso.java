package com.example.proyecto_shem.entity;

import android.os.Build;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class Ingreso implements Serializable {

    private int idIngreso;
    private TipoUsuario tipoUsuario;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String codUsuario;
    private String autorizo;
    private String imgUsuario;
    private Date fechaIngreso;
    private String horaIngreso;
    private TipoMicromovilidad tipoMicromovilidad;

    // Constructor vacío
    public Ingreso() {
        this.fechaIngreso = new Date();  // Asigna la fecha actual

        // Verifica si la versión de Android es al menos API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.horaIngreso = LocalTime.now().toString();  // Asigna la hora local si el SDK es >= 26
        } else {
            this.horaIngreso = new Date().toString();  // Usa una alternativa para versiones anteriores
        }
    }

    // Getters y setters
    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
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

    public String getAutorizo() {
        return autorizo;
    }

    public void setAutorizo(String autorizo) {
        this.autorizo = autorizo;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public TipoMicromovilidad getTipoMicromovilidad() {
        return tipoMicromovilidad;
    }

    public void setTipoMicromovilidad(TipoMicromovilidad tipoMicromovilidad) {
        this.tipoMicromovilidad = tipoMicromovilidad;
    }
}
