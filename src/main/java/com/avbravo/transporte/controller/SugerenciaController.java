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
import com.avbravo.transporteejb.datamodel.SugerenciaDataModel;
import com.avbravo.transporteejb.entity.Sugerencia;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.SugerenciaRepository;
import com.avbravo.transporteejb.services.SugerenciaServices;

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
public class SugerenciaController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private SugerenciaDataModel sugerenciaDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Sugerencia sugerencia = new Sugerencia();
    Sugerencia sugerenciaSelected;
    Sugerencia sugerenciaSearch = new Sugerencia();

    //List
    List<Sugerencia> sugerenciaList = new ArrayList<>();

    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    SugerenciaRepository sugerenciaRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    SugerenciaServices sugerenciaServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return sugerenciaRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SugerenciaController() {
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
                    .withRepository(sugerenciaRepository)
                    .withEntity(sugerencia)
                    .withService(sugerenciaServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/sugerencia/details.jasper")
                    .withPathReportAll("/resources/reportes/sugerencia/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true)
                     .withAction("golist")
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    break;

                case "idsugerencia":
                    if (getValueSearch()!= null) {
                        sugerenciaSearch.setIdsugerencia(getValueSearch().toString());
                        doc = new Document("idsugerencia", sugerenciaSearch.getIdsugerencia());
                        sugerenciaList = sugerenciaRepository.findPagination(doc, page, rowPage, new Document("idsugerencia", -1));
                    } else {
                        sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    }

                    break;
                    
                      case "descripcion":
                    if (getValueSearch() != null) {
                        sugerenciaSearch.setDescripcion(getValueSearch().toString());
                        sugerenciaList = sugerenciaRepository.findRegexInTextPagination("descripcion", sugerenciaSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        sugerenciaSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", sugerenciaSearch.getActivo());
                        sugerenciaList = sugerenciaRepository.findPagination(doc, page, rowPage, new Document("idsugerencia", -1));
                    } else {
                        sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    break;
            }

            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>
    
       // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = sugerenciaServices.isDeleted(sugerencia);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
         Boolean delete = sugerenciaServices.isDeleted(sugerencia);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }
    // </editor-fold>     
}
