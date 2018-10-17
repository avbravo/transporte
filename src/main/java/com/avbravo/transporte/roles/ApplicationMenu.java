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
    private Boolean menuBarAdministracion = false;
    private Boolean menuBarCombustible = false;
    private Boolean menuBarMantenimiento = false;
    private Boolean menuBarSolicitud = false;
    private Boolean menuBarRegistros = false;
    private Boolean menuBarReportes = false;
    
    private Boolean menuBarViajes = false;

    /*
     elementos
     */
     MenuElement carrera = new MenuElement();
    MenuElement calendario = new MenuElement();
    MenuElement calendariobuses = new MenuElement();
    MenuElement calendarioautos = new MenuElement();
    MenuElement conductor = new MenuElement();
    MenuElement conductorcambiarcedula = new MenuElement();
   
    MenuElement estatus = new MenuElement();
    MenuElement facultad = new MenuElement();
    MenuElement tiposolicitud = new MenuElement();
    MenuElement tipovehiculo = new MenuElement();
    MenuElement rol = new MenuElement();
    MenuElement semestre = new MenuElement();
    MenuElement solicitud = new MenuElement();
    MenuElement solicitudManual = new MenuElement();
    MenuElement solicitudDocente = new MenuElement();
    MenuElement solicitudDocentePorAdministrador = new MenuElement();
    MenuElement solicitudAdministrativo = new MenuElement();
    MenuElement solicitudAdministrativoPorAdministrador = new MenuElement();
    MenuElement vehiculo = new MenuElement();
    MenuElement vehiculocambiarplaca = new MenuElement();
    MenuElement viajes = new MenuElement();
    MenuElement usuario = new MenuElement();
    MenuElement unidad = new MenuElement();

    public void enabledAll(Boolean activo) {
                menuBarAdministracion = activo;
                menuBarCombustible = activo;
                menuBarMantenimiento = activo;
        menuBarSolicitud = activo;
        menuBarRegistros = activo;
        menuBarReportes = activo;

         calendario.initialize(activo);
         calendarioautos.initialize(activo);
         calendariobuses.initialize(activo);
        carrera.initialize(activo);
       
        conductor.initialize(activo);
        conductorcambiarcedula.initialize(activo);
        estatus.initialize(activo);
        facultad.initialize(activo);
        tiposolicitud.initialize(activo);
        tipovehiculo.initialize(activo);
        rol.initialize(activo);
        semestre.initialize(activo);
        solicitud.initialize(activo);
        solicitudManual.initialize(activo);
        solicitudDocente.initialize(activo);
        solicitudDocentePorAdministrador.initialize(activo);
        solicitudAdministrativo.initialize(activo);
        solicitudAdministrativoPorAdministrador.initialize(activo);
        vehiculo.initialize(activo);
        vehiculocambiarplaca.initialize(activo);
        viajes.initialize(activo);
        unidad.initialize(activo);
        usuario.initialize(activo);

    }

    public Boolean getMenuBarCombustible() {
        return menuBarCombustible;
    }

    public void setMenuBarCombustible(Boolean menuBarCombustible) {
        this.menuBarCombustible = menuBarCombustible;
    }

    public Boolean getMenuBarMantenimiento() {
        return menuBarMantenimiento;
    }

    public void setMenuBarMantenimiento(Boolean menuBarMantenimiento) {
        this.menuBarMantenimiento = menuBarMantenimiento;
    }

    
    
    
    public MenuElement getSolicitudManual() {
        return solicitudManual;
    }

    public void setSolicitudManual(MenuElement solicitudManual) {
        this.solicitudManual = solicitudManual;
    }

    public MenuElement getCalendariobuses() {
        return calendariobuses;
    }

    public void setCalendariobuses(MenuElement calendariobuses) {
        this.calendariobuses = calendariobuses;
    }

    public MenuElement getCalendario() {
        return calendario;
    }

    public void setCalendario(MenuElement calendario) {
        this.calendario = calendario;
    }

    public MenuElement getCalendarioautos() {
        return calendarioautos;
    }

    public void setCalendarioautos(MenuElement calendarioautos) {
        this.calendarioautos = calendarioautos;
    }
    
    
    
    

    public MenuElement getSemestre() {
        return semestre;
    }

    public void setSemestre(MenuElement semestre) {
        this.semestre = semestre;
    }

    public MenuElement getConductorcambiarcedula() {
        return conductorcambiarcedula;
    }

    public void setConductorcambiarcedula(MenuElement conductorcambiarcedula) {
        this.conductorcambiarcedula = conductorcambiarcedula;
    }

    public MenuElement getVehiculocambiarplaca() {
        return vehiculocambiarplaca;
    }

    public void setVehiculocambiarplaca(MenuElement vehiculocambiarplaca) {
        this.vehiculocambiarplaca = vehiculocambiarplaca;
    }

    public MenuElement getCarrera() {
        return carrera;
    }

    public void setCarrera(MenuElement carrera) {
        this.carrera = carrera;
    }

    public MenuElement getFacultad() {
        return facultad;
    }

    public void setFacultad(MenuElement facultad) {
        this.facultad = facultad;
    }

    public Boolean getMenuBarViajes() {
        return menuBarViajes;
    }

    public void setMenuBarViajes(Boolean menuBarViajes) {
        this.menuBarViajes = menuBarViajes;
    }

    public MenuElement getSolicitudDocente() {
        return solicitudDocente;
    }

    public void setSolicitudDocente(MenuElement solicitudDocente) {
        this.solicitudDocente = solicitudDocente;
    }

    public MenuElement getSolicitudAdministrativo() {
        return solicitudAdministrativo;
    }

    public void setSolicitudAdministrativo(MenuElement solicitudAdministrativo) {
        this.solicitudAdministrativo = solicitudAdministrativo;
    }

    public MenuElement getSolicitudDocentePorAdministrador() {
        return solicitudDocentePorAdministrador;
    }

    public void setSolicitudDocentePorAdministrador(MenuElement solicitudDocentePorAdministrador) {
        this.solicitudDocentePorAdministrador = solicitudDocentePorAdministrador;
    }

    public MenuElement getSolicitudAdministrativoPorAdministrador() {
        return solicitudAdministrativoPorAdministrador;
    }

    public void setSolicitudAdministrativoPorAdministrador(MenuElement solicitudAdministrativoPorAdministrador) {
        this.solicitudAdministrativoPorAdministrador = solicitudAdministrativoPorAdministrador;
    }

    public MenuElement getTiposolicitud() {
        return tiposolicitud;
    }

    public void setTiposolicitud(MenuElement tiposolicitud) {
        this.tiposolicitud = tiposolicitud;
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
