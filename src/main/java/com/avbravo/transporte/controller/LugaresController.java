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
 
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.LugaresDataModel;
import com.avbravo.transporteejb.entity.Lugares;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.datamodel.LugaresDataModel;
import com.avbravo.transporteejb.entity.Lugares;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.LugaresRepository;
import com.avbravo.transporteejb.repository.LugaresRepository;
import com.avbravo.transporteejb.services.LugaresServices;
import com.avbravo.transporteejb.services.LugaresServices;

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
public class LugaresController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private LugaresDataModel lugaresDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Lugares lugares = new Lugares();
    Lugares lugaresSelected;
    Lugares lugaresSearch = new Lugares();

    //List
    List<Lugares> lugaresList = new ArrayList<>();

    //Repository
    @Inject
    LugaresRepository lugaresRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    LugaresServices lugaresServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return lugaresRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public LugaresController() {
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
                    .withRepository(lugaresRepository)
                    .withEntity(lugares)
                    .withService(lugaresServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/lugares/details.jasper")
                    .withPathReportAll("/resources/reportes/lugares/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true) 
                    .withAction("lugares")
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
            lugaresDataModel = new LugaresDataModel(lugaresList);
            Document doc;

            switch ((String) JmoordbContext.get("searchlugares")) {
                case "_init":
                case "_autocomplete":
                    lugaresList = lugaresRepository.findPagination(page, rowPage);
                    break;

                case "idlugares":
                    if (JmoordbContext.get("_fieldsearchlugares") != null) {
                        lugaresSearch.setIdlugares(JmoordbContext.get("_fieldsearchlugares").toString());
                        doc = new Document("idlugares", lugaresSearch.getIdlugares());
                        lugaresList = lugaresRepository.findPagination(doc, page, rowPage, new Document("idlugares", -1));
                    } else {
                        lugaresList = lugaresRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchlugares") != null) {
                        lugaresSearch.setActivo(JmoordbContext.get("_fieldsearchlugares").toString());
                        doc = new Document("activo", lugaresSearch.getActivo());
                        lugaresList = lugaresRepository.findPagination(doc, page, rowPage, new Document("idlugares", -1));
                    } else {
                        lugaresList = lugaresRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    lugaresList = lugaresRepository.findPagination(page, rowPage);
                    break;
            }

            lugaresDataModel = new LugaresDataModel(lugaresList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}