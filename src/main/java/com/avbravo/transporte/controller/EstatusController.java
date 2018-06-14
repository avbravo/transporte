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
import com.avbravo.transporteejb.datamodel.EstatusDataModel;
import com.avbravo.transporteejb.entity.Estatus;
import com.avbravo.transporteejb.repository.EstatusRepository;
import com.avbravo.transporteejb.repository.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.LookupTransporteejbServices;

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
public class EstatusController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    //DataModel
    private EstatusDataModel estatusDataModel;
    

    Integer page = 1;
    Integer rowPage = 25;
 
    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Estatus estatus;
    Estatus estatusSelected;

    //List
    List<Estatus> estatusList = new ArrayList<>();
    List<Estatus> estatusFiltered = new ArrayList<>();

    //Repository
    @Inject
    EstatusRepository estatusRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
     //Atributos para busquedas
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;
    
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    EstatusServices estatusServices;
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

        return estatusRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public LookupTransporteejbServices getlookupTransporteejbServices() {
        return lookupTransporteejbServices;
    }

    public void setlookupTransporteejbServices(LookupTransporteejbServices lookupTransporteejbServices) {
        this.lookupTransporteejbServices = lookupTransporteejbServices;
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

    public EstatusServices getEstatusServices() {
        return estatusServices;
    }

    public void setEstatusServices(EstatusServices estatusServices) {
        this.estatusServices = estatusServices;
    }

    public List<Estatus> getEstatusList() {
        return estatusList;
    }

    public void setEstatusList(List<Estatus> estatusList) {
        this.estatusList = estatusList;
    }

    public List<Estatus> getEstatusFiltered() {
        return estatusFiltered;
    }

    public void setEstatusFiltered(List<Estatus> estatusFiltered) {
        this.estatusFiltered = estatusFiltered;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Estatus getEstatusSelected() {
        return estatusSelected;
    }

    public void setEstatusSelected(Estatus estatusSelected) {
        this.estatusSelected = estatusSelected;
    }

    public EstatusDataModel getEstatusDataModel() {
        return estatusDataModel;
    }

    public void setEstatusDataModel(EstatusDataModel estatusDataModel) {
        this.estatusDataModel = estatusDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public EstatusController() {
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
String action = loginController.get("estatus");
            String id = loginController.get("idestatus");
            String pageSession = loginController.get("pageestatus");
                //Search
            loginController.put("searchestatus", "_init");
            writable = false;

            estatusList = new ArrayList<>();
            estatusFiltered = new ArrayList<>();
            estatus = new Estatus();
            estatusDataModel = new EstatusDataModel(estatusList);
          
            
            if (id != null) {
                Optional<Estatus> optional = estatusRepository.find("idestatus", id);
                 if (optional.isPresent()) {
                    estatus = optional.get();
                    estatusSelected = estatus;
                    writable = true;
                      
                }
            } 
           if (action != null && action.equals("gonew")) {
                estatus = new Estatus();
                estatusSelected = estatus;
                writable =false;

            }
            if (pageSession != null) 
            {
                page = Integer.parseInt(pageSession);
            }
            Integer c = estatusRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("init() " + e.getLocalizedMessage());
        }
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="reset">

    @Override
    public void reset() {

        RequestContext.getCurrentInstance().reset(":form:content");
        prepare("new",estatus);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">

    public String prepare(String action, Estatus item) {
        String url = "";
        try {
              loginController.put("pageestatus", page.toString());
            loginController.put("estatus", action);

            switch (action) {
                case "new":
                    estatus = new Estatus();
                    estatusSelected = new Estatus();

                    writable = false;
                    break;

                case "view":
               
                        estatusSelected =  item;
                        estatus = estatusSelected;
                        loginController.put("idestatus", estatus.getIdestatus());
                  

                    url = "/pages/estatus/view.xhtml";
                    break;
                    
                case "golist":
                    url = "/pages/estatus/list.xhtml";
                    break;
                    
                case "gonew":
                    url = "/pages/estatus/new.xhtml";
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
            estatusList = new ArrayList<>();
            estatusFiltered = new ArrayList<>();
            estatusList = estatusRepository.findAll();
            estatusFiltered = estatusList;
            estatusDataModel = new EstatusDataModel(estatusList);

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
            if (JsfUtil.isVacio(estatus.getIdestatus())) {
                writable = false;
                return "";
            }
            estatus.setIdestatus(estatus.getIdestatus().toUpperCase());
            Optional<Estatus> optional = estatusRepository.findById(estatus);
            if (optional.isPresent()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = estatus.getIdestatus();
                estatus = new Estatus();
                estatus.setIdestatus(id);
                estatusSelected = new Estatus();
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
            estatus.setIdestatus(estatus.getIdestatus().toUpperCase());
            Optional<Estatus> optional = estatusRepository.findById(estatus);
            if (optional.isPresent()) {
               JsfUtil.warningMessage(  rf.getAppMessage("warning.idexist"));
                return null;
            }

            //Lo datos del usuario
            estatus.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (estatusRepository.save(estatus)) {
                  //guarda el contenido anterior
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(estatus.getIdestatus(), loginController.getUsername(),
                    "create", "estatus", estatusRepository.toDocument(estatus).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + estatusRepository.getException().toString());
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

            estatus.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

          
            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(estatus.getIdestatus(), loginController.getUsername(),
                    "update", "estatus", estatusRepository.toDocument(estatus).toString()));

            estatusRepository.update(estatus);
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
            estatus = (Estatus) item;
            
            estatusSelected = estatus;
            if (estatusRepository.delete("idestatus", estatus.getIdestatus())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(estatus.getIdestatus(), loginController.getUsername(), "delete", "estatus", estatusRepository.toDocument(estatus).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    estatusList.remove(estatus);
                    estatusFiltered = estatusList;
                    estatusDataModel = new EstatusDataModel(estatusList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageestatus", page.toString());

                } else {
                    estatus = new Estatus();
                    estatusSelected = new Estatus();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
       // path = deleteonviewpage ? "/pages/estatus/list.xhtml" : "";
       path="";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (estatusRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
             FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pageestatus", page.toString());
            List<Estatus> list = new ArrayList<>();
            list.add(estatus);
            String ruta = "/resources/reportes/estatus/details.jasper";
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
             List<Estatus> list = new ArrayList<>();
            list = estatusRepository.findAll(new Document("idestatus",1));
           
            String ruta = "/resources/reportes/estatus/all.jasper";
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
            estatusList.removeAll(estatusList);
            estatusList.add(estatusSelected);
            estatusFiltered = estatusList;
            estatusDataModel = new EstatusDataModel(estatusList);
             loginController.put("searchestatus", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = estatusRepository.sizeOfPage(rowPage);
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
            if (page < (estatusRepository.sizeOfPage(rowPage))) {
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
                switch (loginController.get("searchestatus")) {
                    case "_init":
                         estatusList = estatusRepository.findPagination(page, rowPage);

                        break;
                    case "_autocomplete":
                        //no se realiza ninguna accion 
                        break;
              
                    case "idestatus":
                        doc = new Document("idestatus", estatus.getIdestatus());
                        estatusList = estatusRepository.findFilterPagination(doc, page, rowPage, new Document("idestatus", -1));
                        break;
                  
                    default:

                     estatusList = estatusRepository.findPagination(page, rowPage);
                        break;
                }
            

            estatusFiltered = estatusList;

            estatusDataModel = new EstatusDataModel(estatusList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

   // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchestatus", "_init");
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

            loginController.put("searchestatus", string);      
      
            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>


}
