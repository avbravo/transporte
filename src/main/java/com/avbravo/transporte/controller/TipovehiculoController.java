/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordb.services.UserInfoServices;
import com.avbravo.transporte.util.ResourcesFiles;
import com.avbravo.transporteejb.datamodel.TipovehiculoDataModel;
import com.avbravo.transporteejb.entity.Tipovehiculo;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.producer.ErrorInfoTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.TipovehiculoRepository;
import com.avbravo.transporteejb.services.TipovehiculoServices;

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
public class TipovehiculoController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private TipovehiculoDataModel tipovehiculoDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Tipovehiculo tipovehiculo;
    Tipovehiculo tipovehiculoSelected;

    //List
    List<Tipovehiculo> tipovehiculoList = new ArrayList<>();
    List<Tipovehiculo> tipovehiculoFiltered = new ArrayList<>();

    //Repository
    @Inject
    TipovehiculoRepository tipovehiculoRepository;
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
    TipovehiculoServices tipovehiculoServices;
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

        return tipovehiculoRepository.listOfPage(rowPage);
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

    public TipovehiculoServices getTipovehiculoServices() {
        return tipovehiculoServices;
    }

    public void setTipovehiculoServices(TipovehiculoServices tipovehiculoServices) {
        this.tipovehiculoServices = tipovehiculoServices;
    }

    public List<Tipovehiculo> getTipovehiculoList() {
        return tipovehiculoList;
    }

    public void setTipovehiculoList(List<Tipovehiculo> tipovehiculoList) {
        this.tipovehiculoList = tipovehiculoList;
    }

    public List<Tipovehiculo> getTipovehiculoFiltered() {
        return tipovehiculoFiltered;
    }

    public void setTipovehiculoFiltered(List<Tipovehiculo> tipovehiculoFiltered) {
        this.tipovehiculoFiltered = tipovehiculoFiltered;
    }

    public Tipovehiculo getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(Tipovehiculo tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public Tipovehiculo getTipovehiculoSelected() {
        return tipovehiculoSelected;
    }

    public void setTipovehiculoSelected(Tipovehiculo tipovehiculoSelected) {
        this.tipovehiculoSelected = tipovehiculoSelected;
    }

    public TipovehiculoDataModel getTipovehiculoDataModel() {
        return tipovehiculoDataModel;
    }

    public void setTipovehiculoDataModel(TipovehiculoDataModel tipovehiculoDataModel) {
        this.tipovehiculoDataModel = tipovehiculoDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public TipovehiculoController() {
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
            String action = loginController.get("tipovehiculo");
            String id = loginController.get("idtipovehiculo");
            String pageSession = loginController.get("pagetipovehiculo");
            //Search

            if (loginController.get("searchtipovehiculo") == null || loginController.get("searchtipovehiculo").equals("")) {
                loginController.put("searchtipovehiculo", "_init");
            }
            writable = false;

            tipovehiculoList = new ArrayList<>();
            tipovehiculoFiltered = new ArrayList<>();
            tipovehiculo = new Tipovehiculo();
            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = tipovehiculoRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        tipovehiculo = new Tipovehiculo();
                        tipovehiculoSelected = tipovehiculo;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Tipovehiculo> optional = tipovehiculoRepository.find("idtipovehiculo", id);
                            if (optional.isPresent()) {
                                tipovehiculo = optional.get();
                                tipovehiculoSelected = tipovehiculo;
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
        prepare("new", tipovehiculo);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Tipovehiculo item) {
        String url = "";
        try {
            loginController.put("pagetipovehiculo", page.toString());
            loginController.put("tipovehiculo", action);

            switch (action) {
                case "new":
                    tipovehiculo = new Tipovehiculo();
                    tipovehiculoSelected = new Tipovehiculo();

                    writable = false;
                    break;

                case "view":

                    tipovehiculoSelected = item;
                    tipovehiculo = tipovehiculoSelected;
                    loginController.put("idtipovehiculo", tipovehiculo.getIdtipovehiculo());

                    url = "/pages/tipovehiculo/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/tipovehiculo/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/tipovehiculo/new.xhtml";
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
            tipovehiculoList = new ArrayList<>();
            tipovehiculoFiltered = new ArrayList<>();
            tipovehiculoList = tipovehiculoRepository.findAll();
            tipovehiculoFiltered = tipovehiculoList;
            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);

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
            if (JsfUtil.isVacio(tipovehiculo.getIdtipovehiculo())) {
                writable = false;
                return "";
            }
            tipovehiculo.setIdtipovehiculo(tipovehiculo.getIdtipovehiculo().toUpperCase());
            Optional<Tipovehiculo> optional = tipovehiculoRepository.findById(tipovehiculo);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = tipovehiculo.getIdtipovehiculo();
                tipovehiculo = new Tipovehiculo();
                tipovehiculo.setIdtipovehiculo(id);
                tipovehiculoSelected = new Tipovehiculo();
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
            tipovehiculo.setIdtipovehiculo(tipovehiculo.getIdtipovehiculo().toUpperCase());
            Optional<Tipovehiculo> optional = tipovehiculoRepository.findById(tipovehiculo);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            tipovehiculo.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (tipovehiculoRepository.save(tipovehiculo)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipovehiculo.getIdtipovehiculo(), loginController.getUsername(),
                        "create", "tipovehiculo", tipovehiculoRepository.toDocument(tipovehiculo).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + tipovehiculoRepository.getException().toString());
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

            tipovehiculo.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipovehiculo.getIdtipovehiculo(), loginController.getUsername(),
                    "update", "tipovehiculo", tipovehiculoRepository.toDocument(tipovehiculo).toString()));

            tipovehiculoRepository.update(tipovehiculo);
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
            tipovehiculo = (Tipovehiculo) item;
            if (!tipovehiculoServices.isDeleted(tipovehiculo)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            tipovehiculoSelected = tipovehiculo;
            if (tipovehiculoRepository.delete("idtipovehiculo", tipovehiculo.getIdtipovehiculo())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(tipovehiculo.getIdtipovehiculo(), loginController.getUsername(), "delete", "tipovehiculo", tipovehiculoRepository.toDocument(tipovehiculo).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    tipovehiculoList.remove(tipovehiculo);
                    tipovehiculoFiltered = tipovehiculoList;
                    tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetipovehiculo", page.toString());

                } else {
                    tipovehiculo = new Tipovehiculo();
                    tipovehiculoSelected = new Tipovehiculo();
                    writable = false;

                }

            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/tipovehiculo/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (tipovehiculoRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagetipovehiculo", page.toString());
            List<Tipovehiculo> list = new ArrayList<>();
            list.add(tipovehiculo);
            String ruta = "/resources/reportes/tipovehiculo/details.jasper";
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
            List<Tipovehiculo> list = new ArrayList<>();
            list = tipovehiculoRepository.findAll(new Document("idtipovehiculo", 1));

            String ruta = "/resources/reportes/tipovehiculo/all.jasper";
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
       tipovehiculoList.removeAll(tipovehiculoList);
            tipovehiculoList.add(tipovehiculoSelected);
            tipovehiculoFiltered = tipovehiculoList;
            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);
            
            loginController.put("searchtipovehiculo", "idtipovehiculo");
            lookupServices.setIdtipovehiculo(tipovehiculoSelected.getIdtipovehiculo());
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = tipovehiculoRepository.sizeOfPage(rowPage);
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
            if (page < (tipovehiculoRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchtipovehiculo")) {
                case "_init":
                       case "_autocomplete":
                    tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);

                    break;

                case "idtipovehiculo":
                    if (lookupServices.getIdtipovehiculo()!= null) {
                          tipovehiculoList = tipovehiculoRepository.findRegexInTextPagination("idtipovehiculo", lookupServices.getIdtipovehiculo(), true, page, rowPage, new Document("idtipovehiculo", -1));
                    } else {
                        tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    }
                  
                    break;

                default:

                    tipovehiculoList = tipovehiculoRepository.findPagination(page, rowPage);
                    break;
            }

            tipovehiculoFiltered = tipovehiculoList;

            tipovehiculoDataModel = new TipovehiculoDataModel(tipovehiculoList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchtipovehiculo", "_init");
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

            loginController.put("searchtipovehiculo", string);

            writable = true;
            move();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(),nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
