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
import com.avbravo.transporteejb.datamodel.RolDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbRepository;

import com.avbravo.transporte.util.LookupServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.services.RolServices;
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
public class RolController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;
    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    private Boolean writable = false;

    //Entity
    Rol rol;
    Rol rolSelected;

    //DataModel
    private RolDataModel rolDataModel;

    //List
    List<Rol> rolList = new ArrayList<>();
    List<Rol> rolFiltered = new ArrayList<>();

    //Repository
    @Inject
    AutoincrementableTransporteejbRepository autoincrementableRepository;
    @Inject
    RolRepository rolRepository;

    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
    
    @Inject
    LookupServices lookupServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    RolServices rolServices;
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

        return rolRepository.listOfPage(rowPage);
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

    public RolServices getRolServices() {
        return rolServices;
    }

    public void setRolServices(RolServices rolServices) {
        this.rolServices = rolServices;
    }

    public List<Rol> getRolList() {
        return rolList;
    }

    public void setRolList(List<Rol> rolList) {
        this.rolList = rolList;
    }

    public List<Rol> getRolFiltered() {
        return rolFiltered;
    }

    public void setRolFiltered(List<Rol> rolFiltered) {
        this.rolFiltered = rolFiltered;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Rol getRolSelected() {
        return rolSelected;
    }

    public void setRolSelected(Rol rolSelected) {
        this.rolSelected = rolSelected;
    }

    public RolDataModel getRolDataModel() {
        return rolDataModel;
    }

    public void setRolDataModel(RolDataModel rolDataModel) {
        this.rolDataModel = rolDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public RolController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="preRenderView()">
    @Override
    public String preRenderView(String action) {
        //acciones al llamar el formulario despuès del init    
        return "";
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">

    @PostConstruct
    public void init() {
        try {
            String action = loginController.get("rol");
            String id = loginController.get("idrol");
            String pageSession = loginController.get("pagerol");
            //Search

            if (loginController.get("searchrol") == null || loginController.get("searchrol").equals("")) {
                loginController.put("searchrol", "_init");
            }

            writable = false;

            rolList = new ArrayList<>();
            rolFiltered = new ArrayList<>();
            rol = new Rol();
            rolDataModel = new RolDataModel(rolList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = rolRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        rol = new Rol();
                        rolSelected = rol;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Rol> optional = rolRepository.find("idrol", id);
                            if (optional.isPresent()) {
                                rol = optional.get();
                                rolSelected = rol;
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
        prepare("new", rol);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Rol item) {
        String url = "";
        try {
            loginController.put("pagerol", page.toString());
            loginController.put("rol", action);
            switch (action) {
                case "new":
                    rol = new Rol();
                    rolSelected = new Rol();

                    writable = false;
                    break;

                case "view":

                    rolSelected = item;
                    rol = rolSelected;
                    loginController.put("idrol", rol.getIdrol());

                    url = "/pages/rol/view.xhtml";
                    break;
                case "golist":

                    url = "/pages/rol/list.xhtml";
                    break;

                case "gonew":
                    rol = new Rol();
                    rolSelected = new Rol();
                    url = "/pages/rol/new.xhtml";
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
            rolList = new ArrayList<>();
            rolFiltered = new ArrayList<>();
            rolList = rolRepository.findAll();
            rolFiltered = rolList;
            rolDataModel = new RolDataModel(rolList);

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
            if (JsfUtil.isVacio(rol.getIdrol())) {
                writable = false;
                return "";
            }
            rol.setIdrol(rol.getIdrol().toUpperCase());
            Optional<Rol> optional = rolRepository.findById(rol);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = rol.getIdrol();
                rol = new Rol();
                rol.setIdrol(id);
                rolSelected = new Rol();
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
            rol.setIdrol(rol.getIdrol().toUpperCase());
            Optional<Rol> optional = rolRepository.findById(rol);
            if (optional.isPresent()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }

            rol.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (rolRepository.save(rol)) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(rol.getIdrol(), loginController.getUsername(),
                        "create", "rol", rolRepository.toDocument(rol).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + rolRepository.getException().toString());
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

            rol.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido anterior
            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(rol.getIdrol(), loginController.getUsername(),
                    "update", "rol", rolRepository.toDocument(rol).toString()));

            rolRepository.update(rol);
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
            rol = (Rol) item;
            if (!rolServices.isDeleted(rol)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            rolSelected = rol;
            if (rolRepository.delete("idrol", rol.getIdrol())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(rol.getIdrol(), loginController.getUsername(), "delete", "rol", rolRepository.toDocument(rol).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    rolList.remove(rol);
                    rolFiltered = rolList;
                    rolDataModel = new RolDataModel(rolList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagerol", page.toString());

                } else {
                    rol = new Rol();
                    rolSelected = new Rol();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        //path = deleteonviewpage ? "/pages/rol/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (rolRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagerol", page.toString());
            List<Rol> list = new ArrayList<>();
            list.add(rol);
            String ruta = "/resources/reportes/rol/details.jasper";
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
            List<Rol> list = new ArrayList<>();
            list = rolRepository.findAll(new Document("idrol", 1));

            String ruta = "/resources/reportes/rol/all.jasper";
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
           rolList.removeAll(rolList);
            rolList.add(rolSelected);
            rolFiltered = rolList;
           rolDataModel = new RolDataModel(rolList);
            
            loginController.put("searchrol", "idrol");
            lookupServices.setIdrol(rolSelected.getIdrol());
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleAutocompleteOfListXhtml() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = rolRepository.sizeOfPage(rowPage);
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
            if (page < (rolRepository.sizeOfPage(rowPage))) {
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

    // <editor-fold defaultstate="collapsed" desc="searchBy(String string)">
    @Override
    public String searchBy(String string) {
        try {
            loginController.put("searchrol", string);
            loginController.put("betweendaterol", "false");
            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="clear">

    @Override
    public String clear() {
        try {
            loginController.put("searchrol", "_init");
            page = 1;
            move();
        } catch (Exception e) {
            JsfUtil.errorMessage("clear()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="move">

    @Override
    public void move() {
        try {

            Document doc;
            switch (loginController.get("searchrol")) {
                case "_init":
                       case "_autocomplete":
                    rolList = rolRepository.findPagination(page, rowPage);

                    break;
       

                case "idrol":
                    if (lookupServices.getIdrol() != null) {
                         doc = new Document("idrol", lookupServices.getIdrol());
                    rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                    } else {
                         rolList = rolRepository.findPagination(page, rowPage);
                    }
                   
                    break;

                default:

                    rolList = rolRepository.findPagination(page, rowPage);
                    break;
            }

            rolFiltered = rolList;

            rolDataModel = new RolDataModel(rolList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>
    
    public Integer tam(){
       return rolRepository.findAll().size();
    }
}
