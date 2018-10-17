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
import com.avbravo.transporteejb.datamodel.VehiculoDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.producer.AutoincrementableTransporteejbServices;
import com.avbravo.transporteejb.producer.ReferentialIntegrityTransporteejbServices;
import com.avbravo.transporteejb.producer.LookupTransporteejbServices;
import com.avbravo.transporteejb.producer.RevisionHistoryTransporteejbRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.services.VehiculoServices;
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
public class VehiculoController implements Serializable, IController {
// <editor-fold defaultstate="collapsed" desc="fields">  

    private static final long serialVersionUID = 1L;

//    @Inject
//private transient ExternalContext externalContext;
    private Boolean writable = false;
    private String placanueva;
    //DataModel
    private VehiculoDataModel vehiculoDataModel;

    Integer page = 1;
    Integer rowPage = 25;

    List<Integer> pages = new ArrayList<>();
    //

    //Entity
    Vehiculo vehiculo;
    Vehiculo vehiculoSelected;

    //List
    List<Vehiculo> vehiculoList = new ArrayList<>();
    List<Vehiculo> vehiculoFiltered = new ArrayList<>();

    //Repository
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    RevisionHistoryTransporteejbRepository revisionHistoryTransporteejbRepository;

    //Services
    //Atributos para busquedas
    @Inject
    ReferentialIntegrityTransporteejbServices referentialIntegrityTransporteejbServices;
    @Inject
    LookupTransporteejbServices lookupTransporteejbServices;
    @Inject
    TipovehiculoServices tipovehiculoServices;

    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    UserInfoServices userInfoServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    ResourcesFiles rf;
    @Inject
    Printer printer;
    @Inject
    LoginController loginController;
    @Inject
    AutoincrementableTransporteejbServices autoincrementableTransporteejbServices;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return vehiculoRepository.listOfPage(rowPage);
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public String getPlacanueva() {
        return placanueva;
    }

    public void setPlacanueva(String placanueva) {
        this.placanueva = placanueva;
    }

    public TipovehiculoServices getTipovehiculoServices() {
        return tipovehiculoServices;
    }

    public void setTipovehiculoServices(TipovehiculoServices tipovehiculoServices) {
        this.tipovehiculoServices = tipovehiculoServices;
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

    public VehiculoServices getVehiculoServices() {
        return vehiculoServices;
    }

    public void setVehiculoServices(VehiculoServices vehiculoServices) {
        this.vehiculoServices = vehiculoServices;
    }

    public List<Vehiculo> getVehiculoList() {
        return vehiculoList;
    }

    public void setVehiculoList(List<Vehiculo> vehiculoList) {
        this.vehiculoList = vehiculoList;
    }

    public List<Vehiculo> getVehiculoFiltered() {
        return vehiculoFiltered;
    }

    public void setVehiculoFiltered(List<Vehiculo> vehiculoFiltered) {
        this.vehiculoFiltered = vehiculoFiltered;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Vehiculo getVehiculoSelected() {
        return vehiculoSelected;
    }

    public void setVehiculoSelected(Vehiculo vehiculoSelected) {
        this.vehiculoSelected = vehiculoSelected;
    }

    public VehiculoDataModel getVehiculoDataModel() {
        return vehiculoDataModel;
    }

    public void setVehiculoDataModel(VehiculoDataModel vehiculoDataModel) {
        this.vehiculoDataModel = vehiculoDataModel;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public LookupTransporteejbServices getLookupTransporteejbServices() {
        return lookupTransporteejbServices;
    }

    public void setLookupTransporteejbServices(LookupTransporteejbServices lookupTransporteejbServices) {
        this.lookupTransporteejbServices = lookupTransporteejbServices;
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public VehiculoController() {
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
            String action = loginController.get("vehiculo");
            String id = loginController.get("idvehiculo");
            String pageSession = loginController.get("pagevehiculo");
            //Search

            if (loginController.get("searchvehiculo") == null || loginController.get("searchvehiculo").equals("")) {
                loginController.put("searchvehiculo", "_init");
            }
            writable = false;

            vehiculoList = new ArrayList<>();
            vehiculoFiltered = new ArrayList<>();
            vehiculo = new Vehiculo();
            vehiculoDataModel = new VehiculoDataModel(vehiculoList);

            if (pageSession != null) {
                page = Integer.parseInt(pageSession);
            }
            Integer c = vehiculoRepository.sizeOfPage(rowPage);
            page = page > c ? c : page;
            if (action != null) {
                switch (action) {
                    case "gonew":
                        vehiculo = new Vehiculo();
                        vehiculoSelected = vehiculo;
                        writable = false;
                        break;
                    case "view":
                        if (id != null) {
                            Optional<Vehiculo> optional = vehiculoRepository.find("idvehiculo", Integer.parseInt(id));
                            if (optional.isPresent()) {
                                vehiculo = optional.get();
                                vehiculoSelected = vehiculo;
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
        prepare("new", vehiculo);
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="prepare(String action, Object... item)">
    public String prepare(String action, Vehiculo item) {
        String url = "";
        try {
            loginController.put("pagevehiculo", page.toString());
            loginController.put("vehiculo", action);

            switch (action) {
                case "new":
                    vehiculo = new Vehiculo();
                    vehiculoSelected = new Vehiculo();

                    writable = false;
                    break;

                case "view":

                    vehiculoSelected = item;
                    vehiculo = vehiculoSelected;
                    loginController.put("idvehiculo", vehiculo.getIdvehiculo().toString());

                    url = "/pages/vehiculo/view.xhtml";
                    break;

                case "golist":
                    url = "/pages/vehiculo/list.xhtml";
                    break;

                case "gonew":
                    url = "/pages/vehiculo/new.xhtml";
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
            vehiculoList = new ArrayList<>();
            vehiculoFiltered = new ArrayList<>();
            vehiculoList = vehiculoRepository.findAll();
            vehiculoFiltered = vehiculoList;
            vehiculoDataModel = new VehiculoDataModel(vehiculoList);

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
            if (JsfUtil.isVacio(vehiculo.getPlaca())) {
                writable = false;
                return "";
            }
            vehiculo.setPlaca(vehiculo.getPlaca().toUpperCase());
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("placa", vehiculo.getPlaca()));
            if (!list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return "";
            } else {
                String id = vehiculo.getPlaca();
                vehiculo = new Vehiculo();
                vehiculo.setPlaca(id);
                vehiculoSelected = new Vehiculo();
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
            vehiculo.setPlaca(vehiculo.getPlaca().toUpperCase());
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("placa", vehiculo.getPlaca()));
            if (!list.isEmpty()) {
                JsfUtil.warningMessage(rf.getAppMessage("warning.idexist"));
                return null;
            }
            vehiculo.setEnreparacion("no");
            Integer id = autoincrementableTransporteejbServices.getContador("vehiculo");
            vehiculo.setIdvehiculo(id);
            //Lo datos del usuario
            vehiculo.setUserInfo(userInfoServices.generateListUserinfo(loginController.getUsername(), "create"));
            if (vehiculoRepository.save(vehiculo)) {
                //guarda el contenido anterior
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(vehiculo.getIdvehiculo().toString(), loginController.getUsername(),
                        "create", "vehiculo", vehiculoRepository.toDocument(vehiculo).toString()));

                JsfUtil.successMessage(rf.getAppMessage("info.save"));
                reset();
            } else {
                JsfUtil.successMessage("save() " + vehiculoRepository.getException().toString());
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

            vehiculo.getUserInfo().add(userInfoServices.generateUserinfo(loginController.getUsername(), "update"));

            //guarda el contenido actualizado
            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(vehiculo.getIdvehiculo().toString(), loginController.getUsername(),
                    "update", "vehiculo", vehiculoRepository.toDocument(vehiculo).toString()));

            vehiculoRepository.update(vehiculo);
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
            vehiculo = (Vehiculo) item;
            if (!vehiculoServices.isDeleted(vehiculo)) {
                JsfUtil.warningDialog("Delete", rf.getAppMessage("waring.integridadreferencialnopermitida"));
                return "";
            }
            vehiculoSelected = vehiculo;
            if (vehiculoRepository.delete("idvehiculo", vehiculo.getIdvehiculo())) {
                revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(vehiculo.getIdvehiculo().toString(), loginController.getUsername(), "delete", "vehiculo", vehiculoRepository.toDocument(vehiculo).toString()));
                JsfUtil.successMessage(rf.getAppMessage("info.delete"));

                if (!deleteonviewpage) {
                    vehiculoList.remove(vehiculo);
                    vehiculoFiltered = vehiculoList;
                    vehiculoDataModel = new VehiculoDataModel(vehiculoList);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagevehiculo", page.toString());

                } else {
                    vehiculo = new Vehiculo();
                    vehiculoSelected = new Vehiculo();
                    writable = false;

                }

            }

        } catch (Exception e) {
            JsfUtil.errorMessage("delete() " + e.getLocalizedMessage());
        }
        // path = deleteonviewpage ? "/pages/vehiculo/list.xhtml" : "";
        path = "";
        return path;
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="deleteAll">
    @Override
    public String deleteAll() {
        if (vehiculoRepository.deleteAll() != 0) {
            JsfUtil.successMessage(rf.getAppMessage("info.delete"));
        }
        return "";
    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="print">

    @Override
    public String print() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pagevehiculo", page.toString());
            List<Vehiculo> list = new ArrayList<>();
            list.add(vehiculo);
            String ruta = "/resources/reportes/vehiculo/details.jasper";
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
            List<Vehiculo> list = new ArrayList<>();
            list = vehiculoRepository.findAll(new Document("idvehiculo", 1));

            String ruta = "/resources/reportes/vehiculo/all.jasper";
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
            vehiculoList.removeAll(vehiculoList);
            vehiculoList.add(vehiculoSelected);
            vehiculoFiltered = vehiculoList;
            vehiculoDataModel = new VehiculoDataModel(vehiculoList);
            loginController.put("searchvehiculo", "_autocomplete");
        } catch (Exception ex) {
            JsfUtil.errorMessage("handleSelect() " + ex.getLocalizedMessage());
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="last">
    @Override
    public String last() {
        try {
            page = vehiculoRepository.sizeOfPage(rowPage);
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
            if (page < (vehiculoRepository.sizeOfPage(rowPage))) {
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
            switch (loginController.get("searchvehiculo")) {
                case "_init":
                    vehiculoList = vehiculoRepository.findPagination(page, rowPage);

                    break;
                case "_autocomplete":
                    //no se realiza ninguna accion 
                    break;

                case "idvehiculo":

                    vehiculoList = vehiculoRepository.findRegexInTextPagination("idvehiculo", lookupTransporteejbServices.getIdvehiculo(), true, page, rowPage, new Document("idvehiculo", -1));
                    break;

                default:

                    vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    break;
            }

            vehiculoFiltered = vehiculoList;

            vehiculoDataModel = new VehiculoDataModel(vehiculoList);

        } catch (Exception e) {
            JsfUtil.errorMessage("move() " + e.getLocalizedMessage());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clear">
    @Override
    public String clear() {
        try {
            loginController.put("searchvehiculo", "_init");
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

            loginController.put("searchvehiculo", string);

            writable = true;
            move();

        } catch (Exception e) {
            JsfUtil.errorMessage("searchBy()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearPlaca()">
    public String clearPlaca() {
        try {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca("");
            writable = false;
        } catch (Exception e) {
            JsfUtil.errorMessage("clearPlaca() " + e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="findByCedula()">
    public String findByPlaca() {
        try {
            if (JsfUtil.isVacio(vehiculo.getPlaca())) {
                writable = false;
                return "";
            }
            vehiculo.setPlaca(vehiculo.getPlaca().toUpperCase());
            writable = true;
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("placa", vehiculo.getPlaca()));
            if (list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idnotexist"));
                return "";
            } else {
                writable = true;
                vehiculo = list.get(0);

                vehiculoSelected = vehiculo;
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("findByPlaca()" + e.getLocalizedMessage());
        }

        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="editPlaca()">
    public String editPlaca() {
        try {

            if (vehiculo.getPlaca().equals(placanueva)) {
                JsfUtil.warningMessage(rf.getMessage("warning.placasiguales"));
                return "";
            }

            Integer c = vehiculoRepository.count(new Document("placa", placanueva));
            if (c >= 1) {
                JsfUtil.warningMessage(rf.getMessage("0"));
                return "";
            }

            vehiculo.setPlaca(placanueva);

            revisionHistoryTransporteejbRepository.save(revisionHistoryServices.getRevisionHistory(vehiculo.getIdvehiculo().toString(), loginController.getUsername(),
                    "update", "conductor", vehiculoRepository.toDocument(vehiculo).toString()));

            vehiculoRepository.update(vehiculo);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            JsfUtil.errorMessage("edit()" + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

}
