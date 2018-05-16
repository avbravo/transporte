/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.roles;

import java.io.Serializable;
import com.avbravo.avbravoutils.menu.MenuElement;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
 
/**
 *
 * @authoravbravo
 */
@Named
@SessionScoped
public class ApplicationMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    public ApplicationMenu() {
    }
    /*
     barra de menu
     */
    private Boolean menuBarSolicitud = false;
    private Boolean menuBarRegistros = false;
    private Boolean menuBarReportes = false;
    private Boolean menuBarAdministracion = false;

    /*
     elementos
     */
    MenuElement conductor = new MenuElement();
    MenuElement estatus = new MenuElement();
    MenuElement tipovehiculo = new MenuElement();
    MenuElement rol = new MenuElement();
    MenuElement solicitud = new MenuElement();
    MenuElement vehiculo = new MenuElement();
    MenuElement viajes = new MenuElement();
    MenuElement usuario = new MenuElement();
    MenuElement unidad = new MenuElement();

    public void enabledAll(Boolean activo) {
        menuBarSolicitud = activo;
        menuBarRegistros = activo;
        menuBarReportes = activo;
        menuBarAdministracion = activo;
        conductor.initialize(activo);
        estatus.initialize(activo);
        tipovehiculo.initialize(activo);
        rol.initialize(activo);
        solicitud.initialize(activo);
        vehiculo.initialize(activo);
        viajes.initialize(activo);
        unidad.initialize(activo);
        usuario.initialize(activo);

    }

    public MenuElement getUnidad() {
        return unidad;
    }

    public void setUnidad(MenuElement unidad) {
        this.unidad = unidad;
    }

    
    
    
    
    public MenuElement getConductor() {
        return conductor;
    }

    public void setConductor(MenuElement conductor) {
        this.conductor = conductor;
    }

    public MenuElement getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(MenuElement tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public MenuElement getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(MenuElement solicitud) {
        this.solicitud = solicitud;
    }

    public MenuElement getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(MenuElement vehiculo) {
        this.vehiculo = vehiculo;
    }

    public MenuElement getViajes() {
        return viajes;
    }

    public void setViajes(MenuElement viajes) {
        this.viajes = viajes;
    }

    
    
    public Boolean getMenuBarSolicitud() {
        return menuBarSolicitud;
    }

    public void setMenuBarSolicitud(Boolean menuBarSolicitud) {
        this.menuBarSolicitud = menuBarSolicitud;
    }

   

    public Boolean getMenuBarRegistros() {
        return menuBarRegistros;
    }

    public void setMenuBarRegistros(Boolean menuBarRegistros) {
        this.menuBarRegistros = menuBarRegistros;
    }

    public Boolean getMenuBarReportes() {
        return menuBarReportes;
    }

    public void setMenuBarReportes(Boolean menuBarReportes) {
        this.menuBarReportes = menuBarReportes;
    }

    public Boolean getMenuBarAdministracion() {
        return menuBarAdministracion;
    }

    public void setMenuBarAdministracion(Boolean menuBarAdministracion) {
        this.menuBarAdministracion = menuBarAdministracion;
    }

    public MenuElement getRol() {
        return rol;
    }

    public void setRol(MenuElement rol) {
        this.rol = rol;
    }

    public MenuElement getUsuario() {
        return usuario;
    }

    public void setUsuario(MenuElement usuario) {
        this.usuario = usuario;
    }

    public MenuElement getEstatus() {
        return estatus;
    }

    public void setEstatus(MenuElement estatus) {
        this.estatus = estatus;
    }

}
