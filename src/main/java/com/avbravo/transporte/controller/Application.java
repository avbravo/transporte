/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

import com.avbravo.ejbjmoordb.pojos.Configuracion;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author avbravo
 */
@Named(value = "application")
@ApplicationScoped
public class Application {

    Configuracion configuracion = new Configuracion();
    /**
     * Creates a new instance of Application
     */
    public Application() {
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }
    
    
    
}
