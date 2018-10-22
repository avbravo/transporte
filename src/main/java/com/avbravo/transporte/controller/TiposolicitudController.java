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
import com.avbravo.transporteejb.datamodel.TiposolicitudDataModel;
import com.avbravo.transporteejb.entity.Tiposolicitud;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.TiposolicitudRepository;
import com.avbravo.transporteejb.services.TiposolicitudServices;

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
public class TiposolicitudController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private TiposolicitudDataModel tiposolicitudDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Tiposolicitud tiposolicitud;
    Tiposolicitud tiposolicitudSelected;

    //List
    List<Tiposolicitud> tiposolicitudList = new ArrayList<>();
    List<Tiposolicitud> tiposolicitudFiltered = new ArrayList<>();

    //Repository
    @Inject
    TiposolicitudRepository tiposolicitudRepository;
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
    TiposolicitudServices tiposolicitudServices;
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

        return tiposolicitudRepository.listOfPage(rowPage);
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

    public TiposolicitudServices getTiposolicitudServices() {
        return tiposolicitudServices;
    }

    public void setTiposolicitudServices(TiposolicitudServices tiposolicitudServices) {
        this.tiposolicitudServices = tiposolicitudServices;
    }

    public List<Tiposolicitud> getTiposolicitudList() {
        return tiposolicitudList;
    }

    public void setTiposolicitudList(List<Tiposolicitud> tiposolicitudList) {
        this.tiposolicitudList = tiposolicitudList;
    }

    public List<Tiposolicitud> getTiposolicitudFiltered() {
        return tiposolicitudFiltered;
    }

    public void setTiposolicitudFiltered(List<Tiposolicitud> tiposolicitudFiltered) {
        this.tiposolicitudFiltered = tiposolicitudFiltered;
    }

    public Tiposolicitud getTiposolicitud() {
        return tiposolicitud;
    }

    public void setTiposolicitud(Tiposolicitud tiposolicitud) {
        this.tiposolicitud = tiposolicitud;
    }

    public Tiposolicitud getTiposolicitudSelected() {
        return tiposolicitudSelected;
    }

    public void setTiposolicitudSelected(Tiposolicitud tiposolicitudSelected) {
        this.tiposolicitudSelected = tiposolicitudSelected;
    }

    public TiposolicitudDataModel getTiposolicitudDataModel() {
        return tiposolicitudDataModel;
    }

    public void setTiposolicitudDataModel(TiposolicitudDataModel tiposolicitudDataModel) {
        this.tiposolicitudDataModel = tiposolicitudDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TiposolicitudController() {
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
            String action = loginController.get("tiposolicitud");
            String id = loginController.get("idtiposolicitud");
            String pageSession = loginController.get("pagetiposolicitud");
            //Search

            if (loginController.get("searchtiposolicitud") == null || loginController.get("searchtiposolicitud").equals("")) {
                loginController.put("searchtiposolicitud", "_init");
            }
            writable = false;

            tiposolicitudList = new ArrayList<>();
            tiposolicitudFiltered = new ArrayList<>();
            tiposolicitud = new Tiposolicitud();
            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = tiposolicitudRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        tiposolicitud = new Tiposolicitud();
                        tiposolicitudSelected = tiposolicitud;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Tiposolicitud> optional = tiposolicitudRepository.find("idtiposolicitud", id);
                            if (optional.isPresent()) {
                                tiposolicitud = optional.get();
                                tiposolicitudSelected = tiposolicitud;
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
        prepare("new", tiposolicitud);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Tiposolicitud item) {
        String url = "";
        try {
            loginController.put("pagetiposolicitud", page.toString());
            loginController.put("tiposolicitud", action);

            switch (action) {
                case "new":
                    tiposolicitud = new Tiposolicitud();
                    tiposolicitudSelected = new Tiposolicitud();

                    writable = false;
                    break;

                case "view":

                    tiposolicitudSelected = item;
                    tiposolicitud = tiposolicitudSelected;
                    loginController.put("idtiposolicitud", tiposolicitud.getIdtiposolicitud());

                    url = "/pages/tiposolicitud/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/tiposolicitud/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/tiposolicitud/new.xhtml";
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
            tiposolicitudList = new ArrayList<>();
            tiposolicitudFiltered = new ArrayList<>();
            tiposolicitudList = tiposolicitudRepository.findAll();
            tiposolicitudFiltered = tiposolicitudList;
            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);

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
            if (JsfUtil.isVacio(tiposolicitud.getIdtiposolicitud())) {
                writable = false;
                return "";
            }
            tiposolicitud.setIdtiposolicitud(tiposolicitud.getIdtiposolicitud().toUpperCase());
            Optional<Tiposolicitud> optional = tiposolicitudRepository.findById(tiposolicitud);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = tiposolicitud.getIdtiposolicitud();
                tiposolicitud = new Tiposolicitud();
                tiposolicitud.setIdtiposolicitud(id);
                tiposolicitudSelected = new Tiposolicitud();
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
            tiposolicitud.setIdtiposolicitud(tiposolicitud.getIdtiposolicitud().toUpperCase());
            Optional<Tiposolicitud> optional = tiposolicitudRepository.findById(tiposolicitud);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            tiposolicitud.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (tiposolicitudRepository.save(tiposolicitud)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tiposolicitud.getIdtiposolicitud(), loginController.getUsername(),
                        "create", "tiposolicitud", tiposolicitudRepository.toDocument(tiposolicitud).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + tiposolicitudRepository.getException().toString());
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

            tiposolicitud.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tiposolicitud.getIdtiposolicitud(), loginController.getUsername(),
                    "update", "tiposolicitud", tiposolicitudRepository.toDocument(tiposolicitud).toString()));

            tiposolicitudRepository.update(tiposolicitud);
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
            tiposolicitud = (Tiposolicitud) item;
            if (!tiposolicitudServices.isDeleted(tiposolicitud)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            tiposolicitudSelected = tiposolicitud;
            if (tiposolicitudRepository.delete("idtiposolicitud", tiposolicitud.getIdtiposolicitud())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tiposolicitud.getIdtiposolicitud(), loginController.getUsername(), "delete", "tiposolicitud", tiposolicitudRepository.toDocument(tiposolicitud).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    tiposolicitudList.remove(tiposolicitud);
                    tiposolicitudFiltered = tiposolicitudList;
                    tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetiposolicitud", page.toString());

                } else {
                    tiposolicitud = new Tiposolicitud();
                    tiposolicitudSelected = new Tiposolicitud();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/tiposolicitud/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (tiposolicitudRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetiposolicitud", page.toString());
            List<Tiposolicitud> list = new ArrayList<>();
            list.add(tiposolicitud);
            String ruta = "/resources/reportes/tiposolicitud/details.jasper";
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
            List<Tiposolicitud> list = new ArrayList<>();
            list = tiposolicitudRepository.findAll(new Document("idtiposolicitud", 1));

            String ruta = "/resources/reportes/tiposolicitud/all.jasper";
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
            tiposolicitudList.removeAll(tiposolicitudList);
            tiposolicitudList.add(tiposolicitudSelected);
            tiposolicitudFiltered = tiposolicitudList;
            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);
            
            loginController.put("searchtiposolicitud", "idtiposolicitud");
            lookupServices.setIdtiposolicitud(tiposolicitudSelected.getIdtiposolicitud());
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = tiposolicitudRepository.sizeOfPage(rowPage);
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
            if (page < (tiposolicitudRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchtiposolicitud")) {
                case "_init":
                    tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;

                case "idtiposolicitud":
                    tiposolicitudList = tiposolicitudRepository.findRegexInTextPagination("idtiposolicitud", lookupServices.getIdtiposolicitud(), true, page, rowPage, new Document("idtiposolicitud", -1));
                    break;

                default:

                    tiposolicitudList = tiposolicitudRepository.findPagination(page, rowPage);
                    break;
            }

            tiposolicitudFiltered = tiposolicitudList;

            tiposolicitudDataModel = new TiposolicitudDataModel(tiposolicitudList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchtiposolicitud", "_init");
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

            loginController.put("searchtiposolicitud", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
