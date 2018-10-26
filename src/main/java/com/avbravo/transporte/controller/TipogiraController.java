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
import com.avbravo.transporteejb.datamodel.TipogiraDataModel;
import com.avbravo.transporteejb.entity.Tipogira;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.TipogiraRepository;
import com.avbravo.transporteejb.services.TipogiraServices;

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
public class TipogiraController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private TipogiraDataModel tipogiraDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Tipogira tipogira;
    Tipogira tipogiraSelected;

    //List
    List<Tipogira> tipogiraList = new ArrayList<>();
    List<Tipogira> tipogiraFiltered = new ArrayList<>();

    //Repository
    @Inject
    TipogiraRepository tipogiraRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
    @Inject
    ReferentialIntegrityTransporteejbServices referentialIntegrityTransporteejbServices;
    @Inject
    LookupServices lookupServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    TipogiraServices tipogiraServices;
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

        return tipogiraRepository.listOfPage(rowPage);
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

    public TipogiraServices getTipogiraServices() {
        return tipogiraServices;
    }

    public void setTipogiraServices(TipogiraServices tipogiraServices) {
        this.tipogiraServices = tipogiraServices;
    }

    public List<Tipogira> getTipogiraList() {
        return tipogiraList;
    }

    public void setTipogiraList(List<Tipogira> tipogiraList) {
        this.tipogiraList = tipogiraList;
    }

    public List<Tipogira> getTipogiraFiltered() {
        return tipogiraFiltered;
    }

    public void setTipogiraFiltered(List<Tipogira> tipogiraFiltered) {
        this.tipogiraFiltered = tipogiraFiltered;
    }

    public Tipogira getTipogira() {
        return tipogira;
    }

    public void setTipogira(Tipogira tipogira) {
        this.tipogira = tipogira;
    }

    public Tipogira getTipogiraSelected() {
        return tipogiraSelected;
    }

    public void setTipogiraSelected(Tipogira tipogiraSelected) {
        this.tipogiraSelected = tipogiraSelected;
    }

    public TipogiraDataModel getTipogiraDataModel() {
        return tipogiraDataModel;
    }

    public void setTipogiraDataModel(TipogiraDataModel tipogiraDataModel) {
        this.tipogiraDataModel = tipogiraDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TipogiraController() {
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
            String action = loginController.get("tipogira");
            String id = loginController.get("idtipogira");
            String pageSession = loginController.get("pagetipogira");
            //Search

            if (loginController.get("searchtipogira") == null || loginController.get("searchtipogira").equals("")) {
                loginController.put("searchtipogira", "_init");
            }
            writable = false;

            tipogiraList = new ArrayList<>();
            tipogiraFiltered = new ArrayList<>();
            tipogira = new Tipogira();
            tipogiraDataModel = new TipogiraDataModel(tipogiraList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = tipogiraRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        tipogira = new Tipogira();
                        tipogiraSelected = tipogira;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Tipogira> optional = tipogiraRepository.find("idtipogira", id);
                            if (optional.isPresent()) {
                                tipogira = optional.get();
                                tipogiraSelected = tipogira;
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
            JsfUtil.errorMessage("init() " + e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
        prepare("new", tipogira);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Tipogira item) {
        String url = "";
        try {
            loginController.put("pagetipogira", page.toString());
            loginController.put("tipogira", action);

            switch (action) {
                case "new":
                    tipogira = new Tipogira();
                    tipogiraSelected = new Tipogira();

                    writable = false;
                    break;

                case "view":

                    tipogiraSelected = item;
                    tipogira = tipogiraSelected;
                    loginController.put("idtipogira", tipogira.getIdtipogira());

                    url = "/pages/tipogira/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/tipogira/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/tipogira/new.xhtml";
                    break;

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("prepare() " + e.getLocalizedMessage());
        }

        return url;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showAll">
    @Override
    public String showAll() {
        try {
            tipogiraList = new ArrayList<>();
            tipogiraFiltered = new ArrayList<>();
            tipogiraList = tipogiraRepository.findAll();
            tipogiraFiltered = tipogiraList;
            tipogiraDataModel = new TipogiraDataModel(tipogiraList);

        } catch (Exception e) {
            JsfUtil.errorMessage("showAll()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="isNew">

    @Override
    public String isNew() {
        try {
            writable = true;
            if (JsfUtil.isVacio(tipogira.getIdtipogira())) {
                writable = false;
                return "";
            }
            tipogira.setIdtipogira(tipogira.getIdtipogira().toUpperCase());
            Optional<Tipogira> optional = tipogiraRepository.findById(tipogira);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = tipogira.getIdtipogira();
                tipogira = new Tipogira();
                tipogira.setIdtipogira(id);
                tipogiraSelected = new Tipogira();
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("isNew()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="save">

    @Override
    public String save() {
        try {
            tipogira.setIdtipogira(tipogira.getIdtipogira().toUpperCase());
            Optional<Tipogira> optional = tipogiraRepository.findById(tipogira);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            tipogira.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (tipogiraRepository.save(tipogira)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipogira.getIdtipogira(), loginController.getUsername(),
                        "create", "tipogira", tipogiraRepository.toDocument(tipogira).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + tipogiraRepository.getException().toString());
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("save()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="edit">

    @Override
    public String edit() {
        try {

            tipogira.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipogira.getIdtipogira(), loginController.getUsername(),
                    "update", "tipogira", tipogiraRepository.toDocument(tipogira).toString()));

            tipogiraRepository.update(tipogira);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            JsfUtil.errorMessage("edit()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="delete(Object item, Boolean deleteonviewpage)">

    @Override
    public String delete(Object item, Boolean deleteonviewpage) {
        String path = "";
        try {
            tipogira = (Tipogira) item;
            if (!tipogiraServices.isDeleted(tipogira)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            tipogiraSelected = tipogira;
            if (tipogiraRepository.delete("idtipogira", tipogira.getIdtipogira())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipogira.getIdtipogira(), loginController.getUsername(), "delete", "tipogira", tipogiraRepository.toDocument(tipogira).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    tipogiraList.remove(tipogira);
                    tipogiraFiltered = tipogiraList;
                    tipogiraDataModel = new TipogiraDataModel(tipogiraList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetipogira", page.toString());

                } else {
                    tipogira = new Tipogira();
                    tipogiraSelected = new Tipogira();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/tipogira/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (tipogiraRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetipogira", page.toString());
            List<Tipogira> list = new ArrayList<>();
            list.add(tipogira);
            String ruta = "/resources/reportes/tipogira/details.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception ex) {
            JsfUtil.errorMessage("imprimir() " + ex.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="printAll">

    @Override
    public String printAll() {
        try {
            List<Tipogira> list = new ArrayList<>();
            list = tipogiraRepository.findAll(new Document("idtipogira", 1));

            String ruta = "/resources/reportes/tipogira/all.jasper";
            HashMap parameters = new HashMap();
            // parameters.put("P_parametro", "valor");
            printer.imprimir(list, ruta, parameters);
        } catch (Exception ex) {
            JsfUtil.errorMessage("imprimir() " + ex.getLocalizedMessage());
        }
        return null;
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="handleSelect">

    public void handleSelect(SelectEvent event) {
        try {
           
        
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="handleAutocompleteOfListXhtml(SelectEvent event)">
    public void handleAutocompleteOfListXhtml(SelectEvent event) {
        try {
       tipogiraList.removeAll(tipogiraList);
            tipogiraList.add(tipogiraSelected);
            tipogiraFiltered = tipogiraList;
            tipogiraDataModel = new TipogiraDataModel(tipogiraList);
            
            loginController.put("searchtipogira", "idtipogira");
            lookupServices.setIdtipogira(tipogiraSelected.getIdtipogira());
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = tipogiraRepository.sizeOfPage(rowPage);
            move();
        } catch (Exception e) {
            JsfUtil.errorMessage("last() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("first() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="next">

    @Override
    public String next() {
        try {
            if (page < (tipogiraRepository.sizeOfPage(rowPage))) {
                page++;
            }
            move();
        } catch (Exception e) {
            JsfUtil.errorMessage("next() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("back() " + e.getLocalizedMessage());
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
            JsfUtil.errorMessage("skip() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move() {

        try {

            Document doc;
            switch (loginController.get("searchtipogira")) {
                case "_init":
                       case "_autocomplete":
                    tipogiraList = tipogiraRepository.findPagination(page, rowPage);

                    break;

                case "idtipogira":
                    if (lookupServices.getIdtipogira()!= null) {
                          tipogiraList = tipogiraRepository.findRegexInTextPagination("idtipogira", lookupServices.getIdtipogira(), true, page, rowPage, new Document("idtipogira", -1));
                    } else {
                        tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    }
                  
                    break;

                default:

                    tipogiraList = tipogiraRepository.findPagination(page, rowPage);
                    break;
            }

            tipogiraFiltered = tipogiraList;

            tipogiraDataModel = new TipogiraDataModel(tipogiraList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchtipogira", "_init");
            page = 1;
            move();
        } catch (Exception e) {
            JsfUtil.errorMessage("clear()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {

            loginController.put("searchtipogira", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
