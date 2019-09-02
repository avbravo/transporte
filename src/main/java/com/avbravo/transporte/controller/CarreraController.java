/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.CarreraDataModel;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;
 
 
import com.avbravo.commonejb.services.CarreraServices;
import com.avbravo.commonejb.services.FacultadServices;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.repository.AutoincrementablebRepository;
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
public class CarreraController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private CarreraDataModel carreraDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Carrera carrera = new Carrera();
    Carrera carreraSelected;
    Carrera carreraSearch = new Carrera();

    //List
    List<Carrera> carreraList = new ArrayList<>();

    //Repository
      @Inject
    AutoincrementablebRepository autoincrementablebRepository;
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    FacultadRepository facultadRepository;
   
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
   @Inject
   FacultadServices facultadServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    CarreraServices carreraServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return carreraRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public CarreraController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del contcarreraler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(carreraRepository)
                    .withEntity(carrera)
                    .withService(carreraServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/carrera/details.jasper")
                    .withPathReportAll("/resources/reportes/carrera/all.jasper")
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
            carreraDataModel = new CarreraDataModel(carreraList);
            Document doc;

            switch ((String) JmoordbContext.get("searchcarrera")) {
                case "_init":
                case "_autocomplete":
                    carreraList = carreraRepository.findPagination(page, rowPage);
                    break;

                case "idcarrera":
                    if (JmoordbContext.get("_fieldsearchcarrera") != null) {
                        carreraSearch.setIdcarrera((Integer) JmoordbContext.get("_fieldsearchcarrera"));
                        doc = new Document("idcarrera", carreraSearch.getIdcarrera());
                        carreraList = carreraRepository.findPagination(doc, page, rowPage, new Document("idcarrera", -1));
                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }

                    break;

                case "descripcion":
                    if (JmoordbContext.get("_fieldsearchcarrera") != null) {
                        carreraSearch.setDescripcion(JmoordbContext.get("_fieldsearchcarrera").toString());
                        //  doc = new Document("descripcion", carreraSearch.getDescripcion());
                        carreraList = carreraRepository.findRegexInTextPagination("descripcion", carreraSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchcarrera") != null) {
                        carreraSearch.setActivo(JmoordbContext.get("_fieldsearchcarrera").toString());
                        doc = new Document("activo", carreraSearch.getActivo());
                        carreraList = carreraRepository.findPagination(doc, page, rowPage, new Document("idcarrera", -1));
                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    carreraList = carreraRepository.findPagination(page, rowPage);
                    break;
            }

            carreraDataModel = new CarreraDataModel(carreraList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            carrera.setIdcarrera(autoincrementableServices.getContador("carrera"));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>
    
    
   
}
