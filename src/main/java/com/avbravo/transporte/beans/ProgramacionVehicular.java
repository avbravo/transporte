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
public class ProgramacionVehicular {
    
    private Integer idviaje;
    private Date fechahorasalida;
    private Date fechahoraregreso;
    private Date fechasolicitud;
    private String numerosolicitudes;
    private String mision;
    private String nombredia;
    private String conductor;
    private String marca;
    private String modelo;
    private String placa;
    private String unidad;
    private String responsable;
    private String solicita;
    private String activo;
    private String realizado;

    public ProgramacionVehicular() {
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getRealizado() {
        return realizado;
    }

    public void setRealizado(String realizado) {
        this.realizado = realizado;
    }

    public ProgramacionVehicular(Integer idviaje, Date fechahorasalida, Date fechahoraregreso, Date fechasolicitud, String numerosolicitudes, String mision, String nombredia, String conductor, String marca, String modelo, String placa, String unidad, String responsable, String solicita, String activo, String realizado) {
        this.idviaje = idviaje;
        this.fechahorasalida = fechahorasalida;
        this.fechahoraregreso = fechahoraregreso;
        this.fechasolicitud = fechasolicitud;
        this.numerosolicitudes = numerosolicitudes;
        this.mision = mision;
        this.nombredia = nombredia;
        this.conductor = conductor;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.unidad = unidad;
        this.responsable = responsable;
        this.solicita = solicita;
        this.activo = activo;
        this.realizado = realizado;
    }

    
    
   

   
    
    
    
    

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getSolicita() {
        return solicita;
    }

    public void setSolicita(String solicita) {
        this.solicita = solicita;
    }

    
    
    
    
    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
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

    public String getNumerosolicitudes() {
        return numerosolicitudes;
    }

    public void setNumerosolicitudes(String numerosolicitudes) {
        this.numerosolicitudes = numerosolicitudes;
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
