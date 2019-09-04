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
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordbutils.printer.Printer;

import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.transporteejb.datamodel.RolDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.services.RolServices;
import com.avbravo.transporteejb.services.UsuarioServices;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
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
public class RolController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private RolDataModel rolDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Rol rol = new Rol();
    Rol rolSelected;
    Rol rolSearch = new Rol();

    //List
    List<Rol> rolList = new ArrayList<>();

    //Repository
    @Inject
    RolRepository rolRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    RolServices rolServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //Notification
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    @Inject
    @Push(channel = "notification")
    private PushContext push;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return rolRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public RolController() {
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
                    .withRepository(rolRepository)
                    .withEntity(rol)
                    .withService(rolServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/rol/details.jasper")
                    .withPathReportAll("/resources/reportes/rol/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    public Boolean afterSave(Boolean saved) {
        try {

            for (Usuario u : usuarioServices.getUsuarioList()) {
                //Guardarlo en la base de datos
                JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
//                Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
                jmoordbNotifications.setIdjmoordbnotifications(autoincrementableServices.getContador("jmoordbnNotifications"));
                jmoordbNotifications.setUsername(u.getUsername());
                jmoordbNotifications.setMessage("se creo un nuevo rol");
                jmoordbNotifications.setViewed("no");
                jmoordbNotifications.setDate(DateUtil.fechaActual());
                jmoordbNotifications.setType("rolnuevo");
                jmoordbNotificationsRepository.save(jmoordbNotifications);
            }
            push.send("Se creo un nuevo rol");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

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
            rolDataModel = new RolDataModel(rolList);
            Document doc;


            switch (getSearch()) {
                case "_init":
                    rolList = rolRepository.findPagination(page, rowPage);
                    break;
                case "_autocomplete":
                    break;

                case "idrol":
                    if (getValueSearch() != null) {
                        rolSearch.setIdrol(getValueSearch().toString());
                        doc = new Document("idrol", rolSearch.getIdrol());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }

                    break;

                case "rol":
                    if (getValueSearch()!= null) {
                        rolSearch.setRol(getValueSearch().toString());
                        rolList = rolRepository.findRegexInTextPagination("rol", rolSearch.getRol(), true, page, rowPage, new Document("rol", -1));

                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        rolSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", rolSearch.getActivo());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    rolList = rolRepository.findPagination(page, rowPage);
                    break;
            }

            rolDataModel = new RolDataModel(rolList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = rolServices.isDeleted(rol);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getAppMessage("warning.nosepuedeeliminarrol"));
        }
        return delete;
    }
    // </editor-fold>     

}
