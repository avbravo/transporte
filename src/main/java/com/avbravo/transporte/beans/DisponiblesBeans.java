/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.beans;

import com.avbravo.transporteejb.entity.Vehiculo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author avbravo
 * Almacena la lista de vehiculos disponibles se usa para mostrarlos en las solicitudes
 */
public class DisponiblesBeans {
    private Date fechahorainicio;
    private Date fechahorafin;
    private Integer numeroBuses;
    private Integer numeroPasajeros;
    private List<Vehiculo> vehiculo;
    private Integer busesRecomendados=0;
    private Integer pasajerosPendientes=0;
    private List<Integer> pasajerosPorViaje = new ArrayList<>();
  
    

    public DisponiblesBeans() {
    }

    public DisponiblesBeans(Date fechahorainicio, Date fechahorafin, Integer numeroBuses, Integer numeroPasajeros, List<Vehiculo> vehiculo) {
        this.fechahorainicio = fechahorainicio;
        this.fechahorafin = fechahorafin;
        this.numeroBuses = numeroBuses;
        this.numeroPasajeros = numeroPasajeros;
        this.vehiculo = vehiculo;
    }

    
    
    public Integer getPasajerosPendientes() {
        return pasajerosPendientes;
    }

    public void setPasajerosPendientes(Integer pasajerosPendientes) {
        this.pasajerosPendientes = pasajerosPendientes;
    }

   
    public List<Integer> getPasajerosPorViaje() {
        return pasajerosPorViaje;
    }

    public void setPasajerosPorViaje(List<Integer> pasajerosPorViaje) {
        this.pasajerosPorViaje = pasajerosPorViaje;
    }

   

    public Integer getBusesRecomendados() {
        return busesRecomendados;
    }

    public void setBusesRecomendados(Integer busesRecomendados) {
        this.busesRecomendados = busesRecomendados;
    }

  
    
    
    
    public List<Vehiculo> getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(List<Vehiculo> vehiculo) {
        this.vehiculo = vehiculo;
    }

   
    public Date getFechahorainicio() {
        return fechahorainicio;
    }

    public void setFechahorainicio(Date fechahorainicio) {
        this.fechahorainicio = fechahorainicio;
    }

    public Date getFechahorafin() {
        return fechahorafin;
    }

    public void setFechahorafin(Date fechahorafin) {
        this.fechahorafin = fechahorafin;
    }

    public Integer getNumeroBuses() {
        return numeroBuses;
    }

    public void setNumeroBuses(Integer numeroBuses) {
        this.numeroBuses = numeroBuses;
    }

    public Integer getNumeroPasajeros() {
        return numeroPasajeros;
    }

    public void setNumeroPasajeros(Integer numeroPasajeros) {
        this.numeroPasajeros = numeroPasajeros;
    }
   
    
    
}
