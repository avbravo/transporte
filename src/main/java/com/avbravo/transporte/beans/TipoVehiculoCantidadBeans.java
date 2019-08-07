/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.beans;

import com.avbravo.transporteejb.entity.Tipovehiculo;

/**
 *
 * @author avbravo Se usa para la solicitud administrativa muestra la lista de
 * tipos de vehiculos y en un datatable el administrativo indica la cantidad de
 * vehiculos que necesita por cada tipo
 */
public class TipoVehiculoCantidadBeans {

    private Tipovehiculo tipovehiculo;
    private Integer cantidad;
    private Integer maximo;
    private Integer pasajeros;

    public TipoVehiculoCantidadBeans() {
    }

    public TipoVehiculoCantidadBeans(Tipovehiculo tipovehiculo, Integer cantidad, Integer maximo, Integer pasajeros) {
        this.tipovehiculo = tipovehiculo;
        this.cantidad = cantidad;
        this.maximo = maximo;
        this.pasajeros = pasajeros;
    }

  

    public Integer getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(Integer pasajeros) {
        this.pasajeros = pasajeros;
    }

    
    
    
    public Integer getMaximo() {
        return maximo;
    }

    public void setMaximo(Integer maximo) {
        this.maximo = maximo;
    }

    public Tipovehiculo getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(Tipovehiculo tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
