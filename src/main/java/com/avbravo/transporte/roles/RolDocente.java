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
public class RolDocente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ApplicationMenu applicationMenu;

    /**
     * Creates a new instance of RolSecretaria
     */
    public RolDocente() {
    }

    public void enabled() {
        /*
         *barra
         */

        applicationMenu.setMenuBarAdministracion(Boolean.FALSE);
        applicationMenu.setMenuBarCalendario(Boolean.FALSE);
        applicationMenu.setMenuBarCombustible(Boolean.FALSE);
 applicationMenu.setMenuBarMantenimiento(Boolean.FALSE);
        applicationMenu.setMenuBarSolicitud(Boolean.TRUE);
        applicationMenu.setMenuBarRegistros(Boolean.FALSE);
        applicationMenu.setMenuBarReportes(Boolean.FALSE);
     
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
        applicationMenu.getSolicitudDocente().initialize(Boolean.TRUE);
        applicationMenu.getSolicitudDocentePorAdministrador().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudAdministrativo().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudAdministrativoPorAdministrador().initialize(Boolean.FALSE);
        applicationMenu.getSugerencia().initialize(Boolean.FALSE);
        applicationMenu.getTipogira().initialize(Boolean.FALSE);
        applicationMenu.getTiposolicitud().initialize(Boolean.FALSE);
        applicationMenu.getTipovehiculo().initialize(Boolean.FALSE);
        applicationMenu.getVehiculo().initialize(Boolean.FALSE);
        applicationMenu.getVehiculocambiarplaca().initialize(Boolean.FALSE);
        applicationMenu.getViajes().initialize(Boolean.FALSE);
        applicationMenu.getUnidad().initialize(Boolean.FALSE);
        applicationMenu.getUsuario().initialize(Boolean.FALSE);

    }

}
