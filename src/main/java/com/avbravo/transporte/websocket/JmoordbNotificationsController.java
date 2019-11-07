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
import com.avbravo.transporteejb.services.UsuarioServices;

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
public class JmoordbNotificationsController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private JmoordbNotificationsDataModel jmoordbNotificationsDataModel;

    String count = "";
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
    @Inject
    UsuarioServices usuarioServices;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return jmoordbNotificationsRepository.listOfPage(rowPage);
    }

    public String getCount() {
        
        return countNotViewed().toString();

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
                    .withAction("golist")
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

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

    // <editor-fold defaultstate="collapsed" desc="String markAsViewed(JmoordbNotifications item)">
    public String markAsViewed(JmoordbNotifications item) {
        try {
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
            //Marca como vistas las notificaciones
            Integer count = 0;
            //update the current notification
            for (JmoordbNotifications jn : jmoordbNotificationsList) {
                if (jn.getIdjmoordbnotifications().equals(item.getIdjmoordbnotifications())) {
                    jmoordbNotificationsList.get(count).setViewed("si");
                    jn.setViewed("si");
                    jmoordbNotificationsRepository.update(jn);
                }
                count++;
            }
            //update the notification_count
            JmoordbContext.put("notification_count", countNotViewed());
            PrimeFaces.current().ajax().update("dropMenuTop");
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
            JsfUtil.successMessage("Marcados como vistos");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="markAsViewedAll()">

    public String markAsViewedAll() {
        try {
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
            //Marca como vistas las notificaciones

            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Document doc = new Document("username", jmoordb_user.getUsername()).append("viewed", "no");
            List<JmoordbNotifications> list = jmoordbNotificationsRepository.findBy(doc);
            list.forEach((jn) -> {
                jn.setViewed("si");
                jmoordbNotificationsRepository.update(jn);
            });
            JmoordbContext.put("notification_count", 0);
            Integer row = 0;
            //Update the datatable page with no in viewed
            for (JmoordbNotifications jn : jmoordbNotificationsList) {
                if (jn.getViewed().equals("no")) {
                    jmoordbNotificationsList.get(row).setViewed("si");
                }
                row++;
            }
            jmoordbNotificationsDataModel = new JmoordbNotificationsDataModel(jmoordbNotificationsList);
            JsfUtil.successMessage("Marcados como vistos");

            PrimeFaces.current().ajax().update("dropMenuTop");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actionWebSocket()">
    public String actionWebSocket() {
        try {
            Integer notification_count = (Integer) JmoordbContext.get("notification_count");
              if (!notification_count.equals(countNotViewed())) {
                JsfUtil.warningMessage("you have a notification " );
                JmoordbContext.put("notification_count",countNotViewed());
            }

        } catch (Exception e) {
            JsfUtil.errorDialog("actionWebSocket()", e.getLocalizedMessage());

        }

        return "";
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Integer countNotViewed()">
    private Integer countNotViewed() {
        try {
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            Document doc = new Document("username", jmoordb_user.getUsername()).append("viewed", "no");
            Integer total = jmoordbNotificationsRepository.count(doc);
            return total;
        } catch (Exception e) {
            JsfUtil.errorDialog("countNotViewed()", e.getLocalizedMessage());
        }
        return 0;
    }// </editor-fold>
    
  // <editor-fold defaultstate="collapsed" desc="metodo()">
    public Usuario generateUsuario(String username){
  
        Usuario usuario = new Usuario();
        try {
            usuario = usuarioServices.findById(username);

        } catch (Exception e) {
             JsfUtil.errorDialog("generateUsuario(()", e.getLocalizedMessage());
        }
        return usuario;
    }
   // </editor-fold>       
}
