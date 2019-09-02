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
import com.avbravo.transporteejb.datamodel.TipovehiculoDataModel;
import com.avbravo.transporteejb.entity.Tipovehiculo;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.TipovehiculoRepository;
import com.avbravo.transporteejb.services.TipovehiculoServices;

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
public class TipovehiculoController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private TipovehiculoDataModel tipovehiculoDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Tipovehiculo tipovehiculo = new Tipovehiculo();
    Tipovehiculo tipovehiculoSelected;
    Tipovehiculo tipovehiculoSearch = new Tipovehiculo();

    //List
    List<Tipovehiculo> tipovehiculoList = new ArrayList<>();

    //Repository
    @Inject
    TipovehiculoRepository tipovehiculoRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    TipovehiculoServices tipovehiculoServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return tipovehiculoRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TipovehiculoController() {
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
                    .withRepository(tipovehiculoRepository)
                    .withEntity(tipovehiculo)
                    .withService(tipovehiculoServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/tipovehiculo/details.jasper")
                    .withPathReportAll("/resources/reportes/tipovehiculo/all.jasper")
                    .withparameters(parameters)
                     .withResetInSave(true) 
                    .withAction("tipovehiculo")
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
            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);
            Document doc;

            switch ((String) JmoordbContext.get("searchtipovehiculo")) {
                case "_init":
                case "_autocomplete":
                    tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    break;

                case "idtipovehiculo":
                    if (JmoordbContext.get("_fieldsearchtipovehiculo") != null) {
                        tipovehiculoSearch.setIdtipovehiculo(JmoordbContext.get("_fieldsearchtipovehiculo").toString());
                        doc = new Document("idtipovehiculo", tipovehiculoSearch.getIdtipovehiculo());
                        tipovehiculoList = tipovehiculoRepository.findPagination(doc, page, rowPage, new Document("idtipovehiculo", -1));
                    } else {
                        tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (JmoordbContext.get("_fieldsearchtipovehiculo") != null) {
                        tipovehiculoSearch.setActivo(JmoordbContext.get("_fieldsearchtipovehiculo").toString());
                        doc = new Document("activo", tipovehiculoSearch.getActivo());
                        tipovehiculoList = tipovehiculoRepository.findPagination(doc, page, rowPage, new Document("idtipovehiculo", -1));
                    } else {
                        tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    break;
            }

            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>
}