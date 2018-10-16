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
public class RolAdministrativo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ApplicationMenu applicationMenu;

    /**
     * Creates a new instance of RolSecretaria
     */
    public RolAdministrativo() {
    }

    public void enabled() {
        /*
         *barra
         */

        applicationMenu.setMenuBarSolicitud(Boolean.TRUE);
        applicationMenu.setMenuBarRegistros(Boolean.FALSE);
        applicationMenu.setMenuBarReportes(Boolean.FALSE);
        applicationMenu.setMenuBarAdministracion(Boolean.FALSE);
        /*
         *menu
         */
     applicationMenu.getCalendario().initialize(Boolean.TRUE);
applicationMenu.getCalendarioautos().initialize(Boolean.TRUE);
applicationMenu.getCalendariobuses().initialize(Boolean.TRUE);
        applicationMenu.getCarrera().initialize(Boolean.FALSE);
        
        applicationMenu.getConductor().initialize(Boolean.FALSE);
        applicationMenu.getConductorcambiarcedula().initialize(Boolean.FALSE);
        applicationMenu.getEstatus().initialize(Boolean.FALSE);
        applicationMenu.getFacultad().initialize(Boolean.FALSE);
        applicationMenu.getRol().initialize(Boolean.FALSE);
        applicationMenu.getSemestre().initialize(Boolean.TRUE);

        applicationMenu.getSolicitud().initialize(Boolean.TRUE);
        applicationMenu.getSolicitudManual().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudDocente().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudDocentePorAdministrador().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudAdministrativo().initialize(Boolean.TRUE);
        applicationMenu.getSolicitudAdministrativoPorAdministrador().initialize(Boolean.FALSE);
        applicationMenu.getTiposolicitud().initialize(Boolean.FALSE);
        applicationMenu.getTipovehiculo().initialize(Boolean.FALSE);
        applicationMenu.getVehiculo().initialize(Boolean.FALSE);
        applicationMenu.getVehiculocambiarplaca().initialize(Boolean.FALSE);
        applicationMenu.getViajes().initialize(Boolean.FALSE);
        applicationMenu.getUnidad().initialize(Boolean.FALSE);
        applicationMenu.getUsuario().initialize(Boolean.FALSE);

    }

}
