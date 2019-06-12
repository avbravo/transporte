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
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.transporteejb.entity.Usuario;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
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

    
    Integer count=0;
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
public Integer getCount(){
     Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
        Document doc = new Document("username",jmoordb_user.getUsername()).append("viewed","no");
        count= jmoordbNotificationsRepository.count(doc);
        return count;
        
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
//            autoincrementablebRepository.setDatabase("commondb");
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
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");

            jmoordbNotificationsSearch.setUsername(jmoordb_user.getUsername());
            jmoordbNotificationsList = jmoordbNotificationsRepository.findRegexInTextPagination("username", jmoordbNotificationsSearch.getUsername(), true, page, rowPage, new Document("idjmoordbnotifications", -1));


            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="marcarComoVistos(JmoordbNotifications item)">
    public String markAsViewed(JmoordbNotifications item){
        try {
             jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
               //Marca como vistas las notificaciones
            Integer count = 0;
            for (JmoordbNotifications jn : jmoordbNotificationsList) {
                if (jn.getIdjmoordbnotifications().equals(item.getIdjmoordbnotifications())) {
                    jmoordbNotificationsList.get(count).setViewed("si");
                    jn.setViewed("si");
                    jmoordbNotificationsRepository.update(jn);
                }
                count++;
            }
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
             JsfUtil.successMessage("Marcados como vistos");
        } catch (Exception e) {
                 errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        String h = "";
        try {
            // h = DateUtil.dateFormatToString(date, "dd/MM/yyyy") ;
            h = DateUtil.dateFormatToString(date, "dd/MM/yyyy") + " " + showHour(date);
        } catch (Exception e) {
            JsfUtil.errorMessage("showDate() " + e.getLocalizedMessage());
        }
        return h;
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        String h = "";
        try {
            h = DateUtil.hourFromDateToString(date);
        } catch (Exception e) {
            JsfUtil.errorMessage("showHour() " + e.getLocalizedMessage());
        }
        return h;
    }// </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="markAsViewedAll()">
    public String markAsViewedAll(){
        try {
             jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
               //Marca como vistas las notificaciones
            Integer count = 0;
            for (JmoordbNotifications jn : jmoordbNotificationsList) {
                if (jn.getViewed().equals("no")) {
                    jmoordbNotificationsList.get(count).setViewed("si");
                    jn.setViewed("si");
                    jmoordbNotificationsRepository.update(jn);
                }
                count++;
            }
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
             JsfUtil.successMessage("Marcados como vistos");
        } catch (Exception e) {
                 errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
