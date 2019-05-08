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
import com.avbravo.jmoordb.pojos.JmoordbEmailMaster;
import com.avbravo.jmoordb.profiles.datamodel.JmoordbEmailMasterDataModel;
import com.avbravo.jmoordb.profiles.repository.JmoordbEmailMasterRepository;
import com.avbravo.jmoordb.services.JmoordbEmailMasterServices;
import com.avbravo.jmoordbutils.JsfUtil;

import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.entity.Usuario;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
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
public class JmoordbEmailMasterController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private JmoordbEmailMasterDataModel jmoordbEmailMasterDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();
    private String passwordnewrepeat;

    //Entity
    JmoordbEmailMaster jmoordbEmailMaster = new JmoordbEmailMaster();
    JmoordbEmailMaster jmoordbEmailMasterSelected;
    JmoordbEmailMaster jmoordbEmailMasterSearch = new JmoordbEmailMaster();

    //List
    List<JmoordbEmailMaster> jmoordbEmailMasterList = new ArrayList<>();

    //Repository
    @Inject
    JmoordbEmailMasterRepository jmoordbEmailMasterRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    JmoordbEmailMasterServices jmoordbEmailMasterServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return jmoordbEmailMasterRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public JmoordbEmailMasterController() {
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
                    .withRepository(jmoordbEmailMasterRepository)
                    .withEntity(jmoordbEmailMaster)
                    .withService(jmoordbEmailMasterServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(true)
                    .withPathReportDetail("/resources/reportes/jmoordbEmailMaster/details.jasper")
                    .withPathReportAll("/resources/reportes/jmoordbEmailMaster/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .build();

            start();
            outlook();

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
            jmoordbEmailMasterDataModel = new JmoordbEmailMasterDataModel(jmoordbEmailMasterList);
            Document doc;

            switch ((String) JmoordbContext.get("searchjmoordbEmailMaster")) {
                case "_init":
                case "_autocomplete":
                    jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    break;

                case "email":
                    if (JmoordbContext.get("_fieldsearchjmoordbEmailMaster") != null) {
                        jmoordbEmailMasterSearch.setEmail(JmoordbContext.get("_fieldsearchjmoordbEmailMaster").toString());
                        doc = new Document("email", jmoordbEmailMasterSearch.getEmail());
                        jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(doc, page, rowPage, new Document("email", -1));
                    } else {
                        jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    }

                    break;

                default:
                    jmoordbEmailMasterList = jmoordbEmailMasterRepository.findPagination(page, rowPage);
                    break;
            }

            jmoordbEmailMasterDataModel = new JmoordbEmailMasterDataModel(jmoordbEmailMasterList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeSave()">
    public Boolean beforeSave() {
        try {
            //password nuevo no coincide
            if (!JsfUtil.isValidEmail(jmoordbEmailMaster.getEmail())) {
                JsfUtil.warningMessage(rf.getMessage("warning.emailnovalido"));
                return false;
            }
            if (!validPassword()) {
                return false;
            }
            if(jmoordbEmailMaster.getMail_smtp_starttls_enable().equals("si")){
                jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
            }
            jmoordbEmailMaster.setPassword(JsfUtil.encriptar(jmoordbEmailMaster.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeEdit()">
    public Boolean beforeEdit() {
        try {

            if (!JsfUtil.isValidEmail(jmoordbEmailMaster.getEmail())) {
                JsfUtil.warningMessage(rf.getMessage("warning.emailnovalido"));
                return false;
            }
jmoordbEmailMaster.setPassword(JsfUtil.encriptar(jmoordbEmailMaster.getPassword()));
            if (!validPassword()) {
                return false;
            }
            
        
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String outlook()">
    public String outlook() {
        try {

            jmoordbEmailMaster.setMail_smtp_host("smtp.office365.com");
            jmoordbEmailMaster.setMail_smtp_auth("true");
            jmoordbEmailMaster.setMail_smtp_port("587");
            jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String gmail()">
    public String gmail() {
        try {

            jmoordbEmailMaster.setMail_smtp_host("smtp.gmail.com");
            jmoordbEmailMaster.setMail_smtp_auth("true");
            jmoordbEmailMaster.setMail_smtp_port("587");
            jmoordbEmailMaster.setMail_smtp_starttls_enable("true");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean validPassword()">
    private Boolean validPassword() {
        try {

            if (jmoordbEmailMaster.getPassword() == null || jmoordbEmailMaster.getPassword().equals("")) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordvacio"));
                return false;
            }
            if (passwordnewrepeat == null || passwordnewrepeat.equals("")) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordrepetidovacio"));
                return false;
            }
            if (!jmoordbEmailMaster.getPassword().equals(passwordnewrepeat)) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return false;
            }
            if (jmoordbEmailMaster.getPassword().length() < 6) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordmenorseis"));
                return false;
            }
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }    // </editor-fold>

   
}
