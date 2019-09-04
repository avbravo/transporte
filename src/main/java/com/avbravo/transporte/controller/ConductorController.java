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
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.transporte.security.LoginController;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.ConductorDataModel;
import com.avbravo.transporteejb.entity.Conductor;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.datamodel.ConductorDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.services.ConductorServices;
import com.avbravo.transporteejb.services.ConductorServices;
import static com.mongodb.client.model.Filters.eq;

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
public class ConductorController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private ConductorDataModel conductorDataModel;
    private String cedulanueva = "";

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Conductor conductor = new Conductor();
    Conductor conductorSelected;
    Conductor conductorSearch = new Conductor();

    //List
    List<Conductor> conductorList = new ArrayList<>();
// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    ConductorServices conductorServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    JmoordbResourcesFiles rf;

    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return conductorRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ConductorController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
// autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(conductorRepository)
                    .withEntity(conductor)
                    .withService(conductorServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/conductor/details.jasper")
                    .withPathReportAll("/resources/reportes/conductor/all.jasper")
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

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            conductorDataModel = new ConductorDataModel(conductorList);
            Document doc;

        
            switch ((String) JmoordbContext.get("searchconductor")) {
                case "_init":
                case "_autocomplete":
                    conductorList = conductorRepository.findPagination(page, rowPage);
                    break;

                case "idconductor":
                    if (JmoordbContext.get("_fieldsearchconductor") != null) {
                        conductorSearch.setIdconductor(Integer.parseInt(JmoordbContext.get("_fieldsearchconductor").toString()));
                        doc = new Document("idconductor", conductorSearch.getIdconductor());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
                case "cedula":
                    if (JmoordbContext.get("_fieldsearchconductor") != null) {
                        conductorSearch.setCedula(JmoordbContext.get("_fieldsearchconductor").toString());
                        doc = new Document("cedula", conductorSearch.getCedula());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
             
                case "nombre":
                    if (JmoordbContext.get("_fieldsearchconductor") != null) {
                        conductorSearch.setNombre(JmoordbContext.get("_fieldsearchconductor").toString());
                        conductorList = conductorRepository.findRegexInTextPagination("nombre", conductorSearch.getNombre(), true, page, rowPage, new Document("nombre", -1));

                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchconductor") != null) {
                        conductorSearch.setActivo(JmoordbContext.get("_fieldsearchconductor").toString());
                        doc = new Document("activo", conductorSearch.getActivo());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    conductorList = conductorRepository.findPagination(page, rowPage);
                    break;
            }

            conductorDataModel = new ConductorDataModel(conductorList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearCedula()">
    public String clearCedula() {
        try {
            conductor = new Conductor();
            conductor.setCedula("");
            writable = false;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="findByCedula()">
    public String findByCedula() {
        try {
            if (JsfUtil.isVacio(conductor.getCedula())) {
                writable = false;
                return "";
            }
            conductor.setCedula(conductor.getCedula().toUpperCase());
            writable = true;
            List<Conductor> list = conductorRepository.findBy(new Document("cedula", conductor.getCedula()));
            if (list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idnotexist"));
                return "";
            } else {
                writable = true;
                conductor = list.get(0);

                conductorSelected = conductor;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }

        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="editCedula">
    public String editCedula() {
        try {

            if (conductor.getCedula().equals(cedulanueva)) {
                JsfUtil.warningMessage(rf.getMessage("warning.cedulasiguales"));
                return "";
            }

            Integer c = conductorRepository.count(new Document("cedula", cedulanueva));
            if (c >= 1) {
                JsfUtil.warningMessage(rf.getMessage("warning.conductorceduladuplicada"));
                return "";
            }

            conductor.setCedula(cedulanueva);
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), jmoordb_user.getUsername(),
                    "update", "conductor", conductorRepository.toDocument(conductor).toString()));

            conductorRepository.update(conductor);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    
    
     // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            conductor.setIdconductor(autoincrementableServices.getContador("conductor"));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>
    
}
