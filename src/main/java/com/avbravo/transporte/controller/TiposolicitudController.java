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
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.TiposolicitudDataModel;
import com.avbravo.transporteejb.entity.Tiposolicitud;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.TiposolicitudRepository;
import com.avbravo.transporteejb.services.TiposolicitudServices;

import java.util.ArrayList;
import java.io.Serializable;
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
public class TiposolicitudController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private TiposolicitudDataModel tiposolicitudDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Tiposolicitud tiposolicitud = new Tiposolicitud();
    Tiposolicitud tiposolicitudSelected;
    Tiposolicitud tiposolicitudSearch = new Tiposolicitud();

    //List
    List<Tiposolicitud> tiposolicitudList = new ArrayList<>();

    //Repository
    @Inject
    TiposolicitudRepository tiposolicitudRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    TiposolicitudServices tiposolicitudServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return tiposolicitudRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TiposolicitudController() {
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
                    .withRepository(tiposolicitudRepository)
                    .withEntity(tiposolicitud)
                    .withService(tiposolicitudServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/tiposolicitud/details.jasper")
                    .withPathReportAll("/resources/reportes/tiposolicitud/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true) 
                    .withAction("tiposolicitud")
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

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);
            Document doc;

            switch ((String) JmoordbContext.get("searchtiposolicitud")) {
                case "_init":
                case "_autocomplete":
                    tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);
                    break;

                case "idtiposolicitud":
                    if (JmoordbContext.get("_fieldsearchtiposolicitud") != null) {
                        tiposolicitudSearch.setIdtiposolicitud(JmoordbContext.get("_fieldsearchtiposolicitud").toString());
                        doc = new Document("idtiposolicitud", tiposolicitudSearch.getIdtiposolicitud());
                        tiposolicitudList = tiposolicitudRepository.findPagination(doc, page, rowPage, new Document("idtiposolicitud", -1));
                    } else {
                        tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchtiposolicitud") != null) {
                        tiposolicitudSearch.setActivo(JmoordbContext.get("_fieldsearchtiposolicitud").toString());
                        doc = new Document("activo", tiposolicitudSearch.getActivo());
                        tiposolicitudList = tiposolicitudRepository.findPagination(doc, page, rowPage, new Document("idtiposolicitud", -1));
                    } else {
                        tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);
                    break;
            }

            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}
