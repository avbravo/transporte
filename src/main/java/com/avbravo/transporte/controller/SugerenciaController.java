/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.printer.Printer;
import com.avbravo.ejbjmoordb.interfaces.IController;
import com.avbravo.ejbjmoordb.services.RevisionHistoryServices;
import com.avbravo.ejbjmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.SugerenciaDataModel;
import com.avbravo.transporteejb.entity.Sugerencia;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.SugerenciaRepository;
import com.avbravo.transporteejb.services.SugerenciaServices;

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
import org.bson.Document;
import org.primefaces.context.RequestContext;

import org.primefaces.event.SelectEvent;
// </editor-fold>

/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
public class SugerenciaController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private SugerenciaDataModel sugerenciaDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Sugerencia sugerencia;
    Sugerencia sugerenciaSelected;

    //List
    List<Sugerencia> sugerenciaList = new ArrayList<>();
    List<Sugerencia> sugerenciaFiltered = new ArrayList<>();

    //Repository
    @Inject
    SugerenciaRepository sugerenciaRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
   
    @Inject
    LookupServices lookupServices;
   @Inject
ErrorInfoTransporteejbServices errorServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    SugerenciaServices sugerenciaServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;
    @Inject
    LoginController loginController;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return sugerenciaRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public LookupServices getLookupServices() {
        return lookupServices;
    }

    public void setLookupServices(LookupServices lookupServices) {
        this.lookupServices = lookupServices;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRowPage() {
        return rowPage;
    }

    public void setRowPage(Integer rowPage) {
        this.rowPage = rowPage;
    }

    public SugerenciaServices getSugerenciaServices() {
        return sugerenciaServices;
    }

    public void setSugerenciaServices(SugerenciaServices sugerenciaServices) {
        this.sugerenciaServices = sugerenciaServices;
    }

    public List<Sugerencia> getSugerenciaList() {
        return sugerenciaList;
    }

    public void setSugerenciaList(List<Sugerencia> sugerenciaList) {
        this.sugerenciaList = sugerenciaList;
    }

    public List<Sugerencia> getSugerenciaFiltered() {
        return sugerenciaFiltered;
    }

    public void setSugerenciaFiltered(List<Sugerencia> sugerenciaFiltered) {
        this.sugerenciaFiltered = sugerenciaFiltered;
    }

    public Sugerencia getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(Sugerencia sugerencia) {
        this.sugerencia = sugerencia;
    }

    public Sugerencia getSugerenciaSelected() {
        return sugerenciaSelected;
    }

    public void setSugerenciaSelected(Sugerencia sugerenciaSelected) {
        this.sugerenciaSelected = sugerenciaSelected;
    }

    public SugerenciaDataModel getSugerenciaDataModel() {
        return sugerenciaDataModel;
    }

    public void setSugerenciaDataModel(SugerenciaDataModel sugerenciaDataModel) {
        this.sugerenciaDataModel = sugerenciaDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SugerenciaController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="preRenderView()">
    @Override
    public String preRenderView(String action) {
        //acciones al llamar el formulario despues del init    
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">

    @PostConstruct
    public void init() {
        try {
            String action = loginController.get("sugerencia");
            String id = loginController.get("idsugerencia");
            String pageSession = loginController.get("pagesugerencia");
            //Search

            if (loginController.get("searchsugerencia") == null || loginController.get("searchsugerencia").equals("")) {
                loginController.put("searchsugerencia", "_init");
            }
            writable = false;

            sugerenciaList = new ArrayList<>();
            sugerenciaFiltered = new ArrayList<>();
            sugerencia = new Sugerencia();
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = sugerenciaRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        sugerencia = new Sugerencia();
                        sugerenciaSelected = sugerencia;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Sugerencia> optional = sugerenciaRepository.find("idsugerencia", id);
                            if (optional.isPresent()) {
                                sugerencia = optional.get();
                                sugerenciaSelected = sugerencia;
                                writable = true;

                            }
                        }
                        break;
                    case "golist":
                        move();
                        break;
                }
            } else {
                move();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
        prepare("new", sugerencia);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Sugerencia item) {
        String url = "";
        try {
            loginController.put("pagesugerencia", page.toString());
            loginController.put("sugerencia", action);

            switch (action) {
                case "new":
                    sugerencia = new Sugerencia();
                    sugerenciaSelected = new Sugerencia();

                    writable = false;
                    break;

                case "view":

                    sugerenciaSelected = item;
                    sugerencia = sugerenciaSelected;
                    loginController.put("idsugerencia", sugerencia.getIdsugerencia());

                    url = "/pages/sugerencia/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/sugerencia/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/sugerencia/new.xhtml";
                    break;

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }

        return url;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showAll">
    @Override
    public String showAll() {
        try {
            sugerenciaList = new ArrayList<>();
            sugerenciaFiltered = new ArrayList<>();
            sugerenciaList = sugerenciaRepository.findAll();
            sugerenciaFiltered = sugerenciaList;
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">

    @Override
    public String isNew() {
        try {
            writable = true;
            if (JsfUtil.isVacio(sugerencia.getIdsugerencia())) {
                writable = false;
                return "";
            }
            sugerencia.setIdsugerencia(sugerencia.getIdsugerencia().toUpperCase());
            Optional<Sugerencia> optional = sugerenciaRepository.findById(sugerencia);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = sugerencia.getIdsugerencia();
                sugerencia = new Sugerencia();
                sugerencia.setIdsugerencia(id);
                sugerenciaSelected = new Sugerencia();
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
            sugerencia.setIdsugerencia(sugerencia.getIdsugerencia().toUpperCase());
            Optional<Sugerencia> optional = sugerenciaRepository.findById(sugerencia);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            sugerencia.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (sugerenciaRepository.save(sugerencia)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(sugerencia.getIdsugerencia(), loginController.getUsername(),
                        "create", "sugerencia", sugerenciaRepository.toDocument(sugerencia).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + sugerenciaRepository.getException().toString());
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="edit">

    @Override
    public String edit() {
        try {

            sugerencia.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(sugerencia.getIdsugerencia(), loginController.getUsername(),
                    "update", "sugerencia", sugerenciaRepository.toDocument(sugerencia).toString()));

            sugerenciaRepository.update(sugerencia);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage)">

    @Override
    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            sugerencia = (Sugerencia) item;
            if (!sugerenciaServices.isDeleted(sugerencia)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            sugerenciaSelected = sugerencia;
            if (sugerenciaRepository.delete("idsugerencia", sugerencia.getIdsugerencia())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(sugerencia.getIdsugerencia(), loginController.getUsername(), "delete", "sugerencia", sugerenciaRepository.toDocument(sugerencia).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    sugerenciaList.remove(sugerencia);
                    sugerenciaFiltered = sugerenciaList;
                    sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagesugerencia", page.toString());

                } else {
                    sugerencia = new Sugerencia();
                    sugerenciaSelected = new Sugerencia();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/sugerencia/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (sugerenciaRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagesugerencia", page.toString());
            List<Sugerencia> list = new ArrayList<>();
            list.add(sugerencia);
            String ruta = "/resources/reportes/sugerencia/details.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="printAll">

    @Override
    public String printAll() {
        try {
            List<Sugerencia> list = new ArrayList<>();
            list = sugerenciaRepository.findAll(new Document("idsugerencia", 1));

            String ruta = "/resources/reportes/sugerencia/all.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {
           
        
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
       sugerenciaList.removeAll(sugerenciaList);
            sugerenciaList.add(sugerenciaSelected);
            sugerenciaFiltered = sugerenciaList;
            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);
            
            loginController.put("searchsugerencia", "idsugerencia");
            lookupServices.setIdsugerencia(sugerenciaSelected.getIdsugerencia());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = sugerenciaRepository.sizeOfPage(rowPage);
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="first">

    @Override
    public String first() {
        try {
            page = 1;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="next">

    @Override
    public String next() {
        try {
            if (page < (sugerenciaRepository.sizeOfPage(rowPage))) {
                page++;
            }
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="back">

    @Override
    public String back() {
        try {
            if (page > 1) {
                page--;
            }
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="skip(Integer page)">

    @Override
    public String skip(Integer page) {
        try {
            this.page = page;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move() {

        try {

            Document doc;
            switch (loginController.get("searchsugerencia")) {
                case "_init":
                       case "_autocomplete":
                    sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);

                    break;

                case "idsugerencia":
                    if (lookupServices.getIdsugerencia()!= null) {
                          sugerenciaList = sugerenciaRepository.findRegexInTextPagination("idsugerencia", lookupServices.getIdsugerencia(), true, page, rowPage, new Document("idsugerencia", -1));
                    } else {
                        sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    }
                  
                    break;

                default:

                    sugerenciaList = sugerenciaRepository.findPagination(page, rowPage);
                    break;
            }

            sugerenciaFiltered = sugerenciaList;

            sugerenciaDataModel = new SugerenciaDataModel(sugerenciaList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchsugerencia", "_init");
            page = 1;
            move();
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {

            loginController.put("searchsugerencia", string);

            writable = true;
            move();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
