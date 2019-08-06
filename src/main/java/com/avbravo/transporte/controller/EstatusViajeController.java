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
import com.avbravo.transporteejb.datamodel.EstatusViajeDataModel;
import com.avbravo.transporteejb.entity.EstatusViaje;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.datamodel.EstatusViajeDataModel;
import com.avbravo.transporteejb.entity.EstatusViaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.EstatusViajeRepository;
import com.avbravo.transporteejb.repository.EstatusViajeRepository;
import com.avbravo.transporteejb.services.EstatusViajeServices;
import com.avbravo.transporteejb.services.EstatusViajeServices;

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
public class EstatusViajeController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private EstatusViajeDataModel estatusViajeDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    EstatusViaje estatusViaje = new EstatusViaje();
    EstatusViaje estatusViajeSelected;
    EstatusViaje estatusViajeSearch = new EstatusViaje();

    //List
    List<EstatusViaje> estatusViajeList = new ArrayList<>();

    //Repository
    @Inject
    EstatusViajeRepository estatusViajeRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    EstatusViajeServices estatusViajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return estatusViajeRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public EstatusViajeController() {
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
                    .withRepository(estatusViajeRepository)
                    .withEntity(estatusViaje)
                    .withService(estatusViajeServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/estatusviaje/details.jasper")
                    .withPathReportAll("/resources/reportes/estatusviaje/all.jasper")
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
            estatusViajeDataModel = new EstatusViajeDataModel(estatusViajeList);
            Document doc;

            switch ((String) JmoordbContext.get("searchestatusViaje")) {
                case "_init":
                case "_autocomplete":
                    estatusViajeList = estatusViajeRepository.findPagination(page, rowPage);
                    break;

                case "idestatusviaje":
                    if (JmoordbContext.get("_fieldsearchestatusViaje") != null) {
                        estatusViajeSearch.setIdestatusviaje(JmoordbContext.get("_fieldsearchestatusViaje").toString());
                        doc = new Document("idestatusviaje", estatusViajeSearch.getIdestatusviaje());
                        estatusViajeList = estatusViajeRepository.findPagination(doc, page, rowPage, new Document("idestatusviaje", -1));
                    } else {
                        estatusViajeList = estatusViajeRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchestatusViaje") != null) {
                        estatusViajeSearch.setActivo(JmoordbContext.get("_fieldsearchestatusViaje").toString());
                        doc = new Document("activo", estatusViajeSearch.getActivo());
                        estatusViajeList = estatusViajeRepository.findPagination(doc, page, rowPage, new Document("idestatusviaje", -1));
                    } else {
                        estatusViajeList = estatusViajeRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    estatusViajeList = estatusViajeRepository.findPagination(page, rowPage);
                    break;
            }

            estatusViajeDataModel = new EstatusViajeDataModel(estatusViajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}