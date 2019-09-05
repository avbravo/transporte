/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.beans;

import java.util.Date;

/**
 *
 * @author avbravo
 */
public class ProgramcionVehicular {
    
    private Integer idviaje;
    private Date fechahorasalida;
    private Date fechahoraregreso;
    private Date fechasolicitud;
    private Integer idsolicitud;
    private String mision;
    private String nombredia;
    private String conductor;
    private String marca;
    private String modelo;
    private String placa;

    public ProgramcionVehicular() {
    }

    public ProgramcionVehicular(Integer idviaje, Date fechahorasalida, Date fechahoraregreso, Date fechasolicitud, Integer idsolicitud, String mision, String nombredia, String conductor, String marca, String modelo, String placa) {
        this.idviaje = idviaje;
        this.fechahorasalida = fechahorasalida;
        this.fechahoraregreso = fechahoraregreso;
        this.fechasolicitud = fechasolicitud;
        this.idsolicitud = idsolicitud;
        this.mision = mision;
        this.nombredia = nombredia;
        this.conductor = conductor;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
    }

    
    
    
    public Integer getIdviaje() {
        return idviaje;
    }

    public void setIdviaje(Integer idviaje) {
        this.idviaje = idviaje;
    }

    public Date getFechahorasalida() {
        return fechahorasalida;
    }

    public void setFechahorasalida(Date fechahorasalida) {
        this.fechahorasalida = fechahorasalida;
    }

    public Date getFechahoraregreso() {
        return fechahoraregreso;
    }

    public void setFechahoraregreso(Date fechahoraregreso) {
        this.fechahoraregreso = fechahoraregreso;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public Integer getIdsolicitud() {
        return idsolicitud;
    }

    public void setIdsolicitud(Integer idsolicitud) {
        this.idsolicitud = idsolicitud;
    }

    public String getMision() {
        return mision;
    }

    public void setMision(String mision) {
        this.mision = mision;
    }

    public String getNombredia() {
        return nombredia;
    }

    public void setNombredia(String nombredia) {
        this.nombredia = nombredia;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    
}
