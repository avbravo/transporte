/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.reportes;

import java.util.Date;

/**
 *
 * @author avbravo
 */
public class ProgramacionFlotaPojo {
   private Integer idprogramacionFlota;

  
   private String diaNombre;
   private Date fecha;
   private String unidad;
   private String mision;
   private String horainicio;
   private String horafin;
   private String conductor;
   private String vehiculo;

    public ProgramacionFlotaPojo() {
    }

   
     public Integer getIdprogramacionFlota() {
        return idprogramacionFlota;
    }

    public void setIdprogramacionFlota(Integer idprogramacionFlota) {
        this.idprogramacionFlota = idprogramacionFlota;
    }
   
    public String getDiaNombre() {
        return diaNombre;
    }

    public void setDiaNombre(String diaNombre) {
        this.diaNombre = diaNombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getMision() {
        return mision;
    }

    public void setMision(String mision) {
        this.mision = mision;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHorafin() {
        return horafin;
    }

    public void setHorafin(String horafin) {
        this.horafin = horafin;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }
   
   
          
   
}
