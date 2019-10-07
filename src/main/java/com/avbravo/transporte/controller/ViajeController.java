/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.ConductorServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class ViajeController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    Date fechaDesde = new Date();
    Date fechaHasta = new Date();
    String lugarDestino = "";
    String comentarios = "";

    //Entity
    Viaje viaje = new Viaje();
    Viaje viajeSelected;
    Viaje viajeSearch = new Viaje();

    Conductor conductor = new Conductor();
    Vehiculo vehiculo = new Vehiculo();

    //List
    List<Viaje> viajeList = new ArrayList<>();
// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    ViajeRepository viajeRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ConductorServices conductorServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    ViajeServices viajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return viajeRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajeController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(viajeRepository)
                    .withEntity(viaje)
                    .withService(viajeServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/viaje/details.jasper")
                    .withPathReportAll("/resources/reportes/viaje/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = viajeServices.isDeleted(viaje);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = viajeServices.isDeleted(viaje);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return viajeServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return viajeServices.showHour(date);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="columnColor(String descripcion )"> 
    public String columnColor(String realizado, String activo) {
        return viajeServices.columnColor(realizado, activo);

    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="handleSelectedConductor(SelectEvent event) ">
    public void handleSelectedConductor(SelectEvent event) {
        try {

            setSearchAndValue("conductor", conductor);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="handleSelectedVehiculo(SelectEvent event) ">

    public void handleSelectedVehiculo(SelectEvent event) {
        try {

            setSearchAndValue("vehiculo", vehiculo);

            move(page);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            viajeDataModel = new ViajeDataModel(viajeList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;

                case "idviaje":
                    if (getValueSearch() != null) {
                        viajeSearch.setIdviaje(Integer.parseInt(getValueSearch().toString()));
                        doc = new Document("idviaje", viajeSearch.getIdviaje());
                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        viajeSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", viajeSearch.getActivo());
                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    }
                    break;

                case "conductor":

                    Conductor conductor = (Conductor) getValueSearch();
                    doc = new Document("activo", "si");
                    doc.append("conductor.idconductor", conductor.getIdconductor());
                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;
                case "vehiculo":

                    Vehiculo vehiculo = (Vehiculo) getValueSearch();
                    doc = new Document("activo", "si");
                    doc.append("vehiculo.idvehiculo", vehiculo.getIdvehiculo());
                    viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));

                    break;
                case "_betweendates":
                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si", "fechahorainicioreserva", fechaDesde, "fechahorafinreserva", fechaHasta, page, rowPage, new Document("idviaje", -1));

                    break;

                case "lugardestino":
                    String lugarDestino = (String) getValueSearch();
                    viajeList = viajeRepository.findRegexInTextPagination("lugardestino", lugarDestino, true, page, rowPage, new Document("idviaje", -1));

                    break;
                case "comentarios":
                    String comentarios = (String) getValueSearch();
                    viajeList = viajeRepository.findRegexInTextPagination("comentarios", comentarios, true, page, rowPage, new Document("idviaje", -1));

                    break;
                default:
                    viajeList = viajeRepository.findPagination(page, rowPage, new Document("idviaje", -1));
                    break;
            }

            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
    
   
}
