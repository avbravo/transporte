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
 
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.TipogiraDataModel;
import com.avbravo.transporteejb.entity.Tipogira;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.TipogiraRepository;
import com.avbravo.transporteejb.services.TipogiraServices;

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
public class TipogiraController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private TipogiraDataModel tipogiraDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Tipogira tipogira = new Tipogira();
    Tipogira tipogiraSelected;
    Tipogira tipogiraSearch = new Tipogira();

    //List
    List<Tipogira> tipogiraList = new ArrayList<>();

    //Repository
    @Inject
    TipogiraRepository tipogiraRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    TipogiraServices tipogiraServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return tipogiraRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TipogiraController() {
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
                    .withRepository(tipogiraRepository)
                    .withEntity(tipogira)
                    .withService(tipogiraServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/tipogira/details.jasper")
                    .withPathReportAll("/resources/reportes/tipogira/all.jasper")
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
            tipogiraDataModel = new TipogiraDataModel(tipogiraList);
            Document doc;

            switch ((String) JmoordbContext.get("searchtipogira")) {
                case "_init":
                case "_autocomplete":
                    tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    break;

                case "idtipogira":
                    if (JmoordbContext.get("_fieldsearchtipogira") != null) {
                        tipogiraSearch.setIdtipogira(JmoordbContext.get("_fieldsearchtipogira").toString());
                        doc = new Document("idtipogira", tipogiraSearch.getIdtipogira());
                        tipogiraList = tipogiraRepository.findPagination(doc, page, rowPage, new Document("idtipogira", -1));
                    } else {
                        tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchtipogira") != null) {
                        tipogiraSearch.setActivo(JmoordbContext.get("_fieldsearchtipogira").toString());
                        doc = new Document("activo", tipogiraSearch.getActivo());
                        tipogiraList = tipogiraRepository.findPagination(doc, page, rowPage, new Document("idtipogira", -1));
                    } else {
                        tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    break;
            }

            tipogiraDataModel = new TipogiraDataModel(tipogiraList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}
