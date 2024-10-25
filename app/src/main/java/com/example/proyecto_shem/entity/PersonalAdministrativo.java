package com.example.proyecto_shem.entity;

import java.io.Serializable;

public class PersonalAdministrativo implements Serializable {

    private int idPersonalAdministrativo;
    private TipoUsuario tipoUsuario;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private TipoCargo cargo;
    private String codAdministrativo;
    private String autorizo;
    private String imgAdministrativo;

    public int getIdPersonalAdministrativo() {
        return idPersonalAdministrativo;
    }

    public void setIdPersonalAdministrativo(int idPersonalAdministrativo) {
        this.idPersonalAdministrativo = idPersonalAdministrativo;
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

    public TipoCargo getCargo() {
        return cargo;
    }

    public void setCargo(TipoCargo cargo) {
        this.cargo = cargo;
    }

    public String getCodAdministrativo() {
        return codAdministrativo;
    }

    public void setCodAdministrativo(String codAdministrativo) {
        this.codAdministrativo = codAdministrativo;
    }

    public String getAutorizo() {
        return autorizo;
    }

    public void setAutorizo(String autorizo) {
        this.autorizo = autorizo;
    }

    public String getImgAdministrativo() {
        return imgAdministrativo;
    }

    public void setImgAdministrativo(String imgAdministrativo) {
        this.imgAdministrativo = imgAdministrativo;
    }
}
