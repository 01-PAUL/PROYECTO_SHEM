package com.example.proyecto_shem.entity;

import java.io.Serializable;
import java.util.Date;

public class DNI implements Serializable {

    private int idDni;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String genero;
    private Date fechaNacimiento;
    private String direccion;
    private String imgUsuarioReniec;

    public int getIdDni() {
        return idDni;
    }

    public void setIdDni(int idDni) {
        this.idDni = idDni;
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImgUsuarioReniec() {
        return imgUsuarioReniec;
    }

    public void setImgUsuarioReniec(String imgUsuarioReniec) {
        this.imgUsuarioReniec = imgUsuarioReniec;
    }
}
