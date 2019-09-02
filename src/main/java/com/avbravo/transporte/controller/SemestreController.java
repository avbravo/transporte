/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.SemestreDataModel;
import com.avbravo.commonejb.entity.Semestre;
import com.avbravo.commonejb.repository.SemestreRepository;
import com.avbravo.commonejb.services.SemestreServices;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
 

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;

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
public class SemestreController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private SemestreDataModel semestreDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Semestre semestre = new Semestre();
    Semestre semestreSelected;
    Semestre semestreSearch = new Semestre();

    //List
    List<Semestre> semestreList = new ArrayList<>();

    //Repository
    @Inject
    SemestreRepository semestreRepository;
    //Services
     @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;   
    @Inject
    SemestreServices semestreServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return semestreRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SemestreController() {
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
                    .withRepository(semestreRepository)
                    .withEntity(semestre)
                    .withService(semestreServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/rol/details.jasper")
                    .withPathReportAll("/resources/reportes/rol/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true)
                    .withAction("semestre")
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
            semestreDataModel = new SemestreDataModel(semestreList);
            Document doc;

            switch ((String) JmoordbContext.get("searchsemestre")) {
                case "_init":
                case "_autocomplete":
                    semestreList = semestreRepository.findPagination(page, rowPage);
                    break;

                case "idsemestre":
                    if (JmoordbContext.get("_fieldsearchsemestre") != null) {
                        semestreSearch.setIdsemestre(JmoordbContext.get("_fieldsearchsemestre").toString());
                        doc = new Document("idsemestre",semestreSearch.getIdsemestre());
                        semestreList = semestreRepository.findPagination(doc, page, rowPage, new Document("idsemestre", -1));
                    } else {
                        semestreList = semestreRepository.findPagination(page, rowPage);
                    }

                    break;
                    
                case "descripcion":
                    if (JmoordbContext.get("_fieldsearchsemestre") != null) {
                        semestreSearch.setIdsemestre(JmoordbContext.get("_fieldsearchsemestre").toString());
                        doc = new Document("descripcion",semestreSearch.getDescripcion());
                        semestreList = semestreRepository.findPagination(doc, page, rowPage, new Document("descripcion", -1));
                    } else {
                        semestreList = semestreRepository.findPagination(page, rowPage);
                    }

                    break;
//                    
//                          case "descripcion":
//                    if (JmoordbContext.get("_fieldsearchsemestre") != null) {
//                        semestreSearch.setDescripcion(JmoordbContext.get("_fieldsearchsemestre").toString());
//                        semestreList = semestreRepository.findRegexInTextPagination("descripcion", semestreSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));
//
//                    } else {
//                        semestreList = semestreRepository.findPagination(page, rowPage);
//                    }
//
//                    break;
              
                default:
                    semestreList = semestreRepository.findPagination(page, rowPage);
                    break;
            }

            semestreDataModel = new SemestreDataModel(semestreList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}