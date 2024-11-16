package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class PermisoSalida implements Serializable {
    private int idPermiso;
    private String usuario;
    private String tipoDocumento;
    private String numDocumento;
    private String genero;
    private String nacionalidad;
    private String imageUrl;
    private String detallePermiso;
    private String area;
    private String fechaSalida;
    private String horaSalida;
    private String micromovilidad;


    public int getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetallePermiso() {
        return detallePermiso;
    }

    public void setDetallePermiso(String detallePermiso) {
        this.detallePermiso = detallePermiso;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getMicromovilidad() {
        return micromovilidad;
    }

    public void setMicromovilidad(String micromovilidad) {
        this.micromovilidad = micromovilidad;
    }
}


