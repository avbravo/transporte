/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.websocket;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.repository.AutoincrementablebRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.datamodel.JmoordbNotificationsDataModel;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordb.services.JmoordbNotificationsServices;

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
public class JmoordbNotificationsController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private JmoordbNotificationsDataModel jmoordbNotificationsDataModel; 

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
    JmoordbNotifications jmoordbNotificationsSelected;
    JmoordbNotifications jmoordbNotificationsSearch = new JmoordbNotifications();

    //List
    List<JmoordbNotifications> jmoordbNotificationsList = new ArrayList<>();

    //Repository
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    AutoincrementablebRepository autoincrementablebRepository;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    JmoordbNotificationsServices jmoordbNotificationsServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return jmoordbNotificationsRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public JmoordbNotificationsController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del contjmoordbNotificationsler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(jmoordbNotificationsRepository)
                    .withEntity(jmoordbNotifications)
                    .withService(jmoordbNotificationsServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/jmoordbNotifications/details.jasper")
                    .withPathReportAll("/resources/reportes/jmoordbNotifications/all.jasper")
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
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
            Document doc;

            switch ((String) JmoordbContext.get("searchjmoordbNotifications")) {
                case "_init":
                case "_autocomplete":
                    jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(page, rowPage);
                    break;

                case "idjmoordbNotifications":
                    if (JmoordbContext.get("_fieldsearchjmoordbNotifications") != null) {
                        jmoordbNotificationsSearch.setIdjoordbnotifications((Integer) JmoordbContext.get("_fieldsearchjmoordbNotifications"));
                        doc = new Document("idjmoordbnotifications", jmoordbNotificationsSearch.getIdjoordbnotifications());
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(doc, page, rowPage, new Document("idjmoordbnotifications", -1));
                    } else {
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(page, rowPage);
                    }

                    break;

                case "username":
                    if (JmoordbContext.get("_fieldsearchjmoordbNotifications") != null) {
                        jmoordbNotificationsSearch.setUsername(JmoordbContext.get("_fieldsearchjmoordbNotifications").toString());
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findRegexInTextPagination("descripcion", jmoordbNotificationsSearch.getUsername(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(page, rowPage);
                    }

                    break;
                case "viewed":
                    if (JmoordbContext.get("_fieldsearchjmoordbNotifications") != null) {
                        jmoordbNotificationsSearch.setViewed(JmoordbContext.get("_fieldsearchjmoordbNotifications").toString());
                        doc = new Document("viewed", jmoordbNotificationsSearch.getViewed());
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(doc, page, rowPage, new Document("idjmoordbnotifications", -1));
                    } else {
                        jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    jmoordbNotificationsList = jmoordbNotificationsRepository.findPagination(page, rowPage);
                    break;
            }

            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            jmoordbNotifications.setIdjoordbnotifications(autoincrementableServices.getContador("jmoordbNotifications"));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>
    
    
}
