/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.FacultadDataModel;
import com.avbravo.commonejb.entity.Facultad;
import com.avbravo.commonejb.repository.FacultadRepository;

import com.avbravo.commonejb.services.FacultadServices;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.repository.AutoincrementablebRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;

import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.entity.Usuario;

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
public class FacultadController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private FacultadDataModel facultadDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Facultad facultad = new Facultad();
    Facultad facultadSelected;
    Facultad facultadSearch = new Facultad();

    //List
    List<Facultad> facultadList = new ArrayList<>();

    //Repository
    @Inject
    FacultadRepository facultadRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    AutoincrementablebRepository autoincrementablebRepository;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    FacultadServices facultadServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return facultadRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public FacultadController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del contfacultadler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(facultadRepository)
                    .withEntity(facultad)
                    .withService(facultadServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/facultad/details.jasper")
                    .withPathReportAll("/resources/reportes/facultad/all.jasper")
                    .withparameters(parameters)
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
            facultadDataModel = new FacultadDataModel(facultadList);
            Document doc;

            switch ((String) JmoordbContext.get("searchfacultad")) {
                case "_init":
                case "_autocomplete":
                    facultadList = facultadRepository.findPagination(page, rowPage);
                    break;

                case "idfacultad":
                    if (JmoordbContext.get("_fieldsearchfacultad") != null) {
                        facultadSearch.setIdfacultad((Integer) JmoordbContext.get("_fieldsearchfacultad"));
                        doc = new Document("idfacultad", facultadSearch.getIdfacultad());
                        facultadList = facultadRepository.findPagination(doc, page, rowPage, new Document("idfacultad", -1));
                    } else {
                        facultadList = facultadRepository.findPagination(page, rowPage);
                    }

                    break;

                case "descripcion":
                    if (JmoordbContext.get("_fieldsearchfacultad") != null) {
                        facultadSearch.setDescripcion(JmoordbContext.get("_fieldsearchfacultad").toString());
                        facultadList = facultadRepository.findRegexInTextPagination("descripcion", facultadSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        facultadList = facultadRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchfacultad") != null) {
                        facultadSearch.setActivo(JmoordbContext.get("_fieldsearchfacultad").toString());
                        doc = new Document("activo", facultadSearch.getActivo());
                        facultadList = facultadRepository.findPagination(doc, page, rowPage, new Document("idfacultad", -1));
                    } else {
                        facultadList = facultadRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    facultadList = facultadRepository.findPagination(page, rowPage);
                    break;
            }

            facultadDataModel = new FacultadDataModel(facultadList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            facultad.setIdfacultad(autoincrementableServices.getContador("facultad"));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>
    
    
}
