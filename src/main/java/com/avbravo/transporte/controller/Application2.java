/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

import com.avbravo.jmoordb.pojos.Configuracion;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author avbravo
 */
@Named(value = "application2")
@ApplicationScoped
public class Application2 {

    Configuracion configuracion = new Configuracion();

    /**
     * Creates a new instance of Application
     */
    public Application2() {
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

}