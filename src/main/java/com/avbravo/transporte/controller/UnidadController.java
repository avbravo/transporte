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
import com.avbravo.jmoordb.interfaces.IControllerOld;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.transporte.security.LoginController;

import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.UnidadDataModel;
import com.avbravo.transporteejb.entity.Unidad;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.datamodel.UnidadDataModel;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.repository.UnidadRepository;
import com.avbravo.transporteejb.services.UnidadServices;
import com.avbravo.transporteejb.services.UnidadServices;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.primefaces.PrimeFaces;
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
public class UnidadController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private UnidadDataModel unidadDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Unidad unidad = new Unidad();
    Unidad unidadSelected;
    Unidad unidadSearch = new Unidad();

    //List
    List<Unidad> unidadList = new ArrayList<>();

    //Repository
    @Inject
    UnidadRepository unidadRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    UnidadServices unidadServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return unidadRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public UnidadController() {
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
                    .withRepository(unidadRepository)
                    .withEntity(unidad)
                    .withService(unidadServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/unidad/details.jasper")
                    .withPathReportAll("/resources/reportes/unidad/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true)
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
            unidadDataModel = new UnidadDataModel(unidadList);
            Document doc;

            switch ((String) JmoordbContext.get("searchunidad")) {
                case "_init":
                case "_autocomplete":
                    unidadList = unidadRepository.findPagination(page, rowPage);
                    break;

                case "idunidad":
                    if (JmoordbContext.get("_fieldsearchunidad") != null) {
                        unidadSearch.setIdunidad(JmoordbContext.get("_fieldsearchunidad").toString());
                        doc = new Document("idunidad", unidadSearch.getIdunidad());
                        unidadList = unidadRepository.findPagination(doc, page, rowPage, new Document("idunidad", -1));
                    } else {
                        unidadList = unidadRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchunidad") != null) {
                        unidadSearch.setActivo(JmoordbContext.get("_fieldsearchunidad").toString());
                        doc = new Document("activo", unidadSearch.getActivo());
                        unidadList = unidadRepository.findPagination(doc, page, rowPage, new Document("idunidad", -1));
                    } else {
                        unidadList = unidadRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    unidadList = unidadRepository.findPagination(page, rowPage);
                    break;
            }

            unidadDataModel = new UnidadDataModel(unidadList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}
