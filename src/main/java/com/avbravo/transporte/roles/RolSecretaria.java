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
public class RolSecretaria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ApplicationMenu applicationMenu;

    /**
     * Creates a new instance of RolSecretaria
     */
    public RolSecretaria() {
    }

    public void enabled() {
        /*
         *barra
         */
           applicationMenu.setMenuBarAdministracion(Boolean.TRUE);
        applicationMenu.setMenuBarCombustible(Boolean.FALSE);
 applicationMenu.setMenuBarMantenimiento(Boolean.FALSE);
        applicationMenu.setMenuBarSolicitud(Boolean.TRUE);
        applicationMenu.setMenuBarRegistros(Boolean.TRUE);
        applicationMenu.setMenuBarReportes(Boolean.TRUE);

        /*
         *menu
         */
        applicationMenu.getCalendario().initialize(Boolean.TRUE);
applicationMenu.getCalendarioautos().initialize(Boolean.TRUE);
applicationMenu.getCalendariobuses().initialize(Boolean.TRUE);

        applicationMenu.getCarrera().initialize(Boolean.TRUE);
        applicationMenu.getConductor().initialize(Boolean.TRUE);
        applicationMenu.getConductorcambiarcedula().initialize(Boolean.FALSE);
        applicationMenu.getEstatus().initialize(Boolean.TRUE);
        applicationMenu.getFacultad().initialize(Boolean.TRUE);
        applicationMenu.getRol().initialize(Boolean.TRUE);
        applicationMenu.getSemestre().initialize(Boolean.TRUE);

        applicationMenu.getSolicitud().initialize(Boolean.TRUE);
        applicationMenu.getSolicitudManual().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudDocente().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudDocentePorAdministrador().initialize(Boolean.TRUE);
        applicationMenu.getSolicitudAdministrativo().initialize(Boolean.FALSE);
        applicationMenu.getSolicitudAdministrativoPorAdministrador().initialize(Boolean.TRUE);
        applicationMenu.getTiposolicitud().initialize(Boolean.TRUE);
        applicationMenu.getTipovehiculo().initialize(Boolean.TRUE);
        applicationMenu.getVehiculo().initialize(Boolean.TRUE);
        applicationMenu.getVehiculocambiarplaca().initialize(Boolean.TRUE);
        applicationMenu.getViajes().initialize(Boolean.TRUE);
        applicationMenu.getUnidad().initialize(Boolean.TRUE);
        applicationMenu.getUsuario().initialize(Boolean.TRUE);

    }

}
