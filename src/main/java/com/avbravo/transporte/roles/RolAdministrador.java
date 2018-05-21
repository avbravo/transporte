/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
package com.avbravo.transporte.roles;

import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 *
 * @authoravbravo
 */
@Named
@RequestScoped
public class RolAdministrador implements Serializable{
private static final long serialVersionUID = 1L;

 @Inject
 ApplicationMenu applicationMenu;
    /**
     * Creates a new instance of RolAdministrador
     */
    public RolAdministrador() {
    }
 public void enabled() {
        /*
         *barra
         */
        
      applicationMenu.setMenuBarSolicitud(Boolean.TRUE);
      applicationMenu.setMenuBarRegistros(Boolean.TRUE);
      applicationMenu.setMenuBarReportes(Boolean.TRUE);
      applicationMenu.setMenuBarAdministracion(Boolean.TRUE);
        /*
         *menu
         */
        
     
      applicationMenu.getConductor().initialize(Boolean.TRUE);
      applicationMenu.getEstatus().initialize(Boolean.TRUE);
      applicationMenu.getRol().initialize(Boolean.TRUE);
      applicationMenu.getTiposolicitud().initialize(Boolean.TRUE);
      applicationMenu.getTipovehiculo().initialize(Boolean.TRUE);
      applicationMenu.getSolicitud().initialize(Boolean.TRUE);
      applicationMenu.getVehiculo().initialize(Boolean.TRUE);
      applicationMenu.getViajes().initialize(Boolean.TRUE);
     applicationMenu.getUnidad().initialize(Boolean.TRUE);
     applicationMenu.getUsuario().initialize(Boolean.TRUE);
          
    }




}
