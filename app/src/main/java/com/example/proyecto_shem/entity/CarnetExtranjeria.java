package com.example.proyecto_shem.entity;

import java.io.Serializable;
import java.util.Date;

public class CarnetExtranjeria implements Serializable {

    private int idCarnetExt;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String genero;
    private Date fechaNacimiento;
    private String nacionalidad;
    private String imgUsuarioExt;

    public int getIdCarnetExt() {
        return idCarnetExt;
    }

    public void setIdCarnetExt(int idCarnetExt) {
        this.idCarnetExt = idCarnetExt;
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

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getImgUsuarioExt() {
        return imgUsuarioExt;
    }

    public void setImgUsuarioExt(String imgUsuarioExt) {
        this.imgUsuarioExt = imgUsuarioExt;
    }
}
