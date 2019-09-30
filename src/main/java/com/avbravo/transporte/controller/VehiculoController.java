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
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.transporteejb.datamodel.VehiculoDataModel;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.services.TipovehiculoServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @authoravbravo
 */
@Named
@ViewScoped
@Getter
@Setter
public class VehiculoController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private VehiculoDataModel vehiculoDataModel;
    private String placanueva = "";

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Vehiculo vehiculo = new Vehiculo();
    Vehiculo vehiculoSelected;
    Vehiculo vehiculoSearch = new Vehiculo();

    //List
    List<Vehiculo> vehiculoList = new ArrayList<>();

    //Repository
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    TipovehiculoServices tipovehiculoServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    RevisionHistoryServices revisionHistoryServices;
    @Inject
    JmoordbResourcesFiles rf;

    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return vehiculoRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public VehiculoController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
// autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(vehiculoRepository)
                    .withEntity(vehiculo)
                    .withService(vehiculoServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/vehiculo/details.jasper")
                    .withPathReportAll("/resources/reportes/vehiculo/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

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
            vehiculoDataModel = new VehiculoDataModel(vehiculoList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    break;

                case "idvehiculo":
                    if (getValueSearch() != null) {
                        vehiculoSearch.setIdvehiculo(Integer.parseInt(getValueSearch().toString()));
                        doc = new Document("idvehiculo", vehiculoSearch.getIdvehiculo());
                        vehiculoList = vehiculoRepository.findPagination(doc, page, rowPage, new Document("placa", -1));
                    } else {
                        vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    }

                    break;
                case "placa":
                    if (getValueSearch() != null) {
                        vehiculoSearch.setPlaca(getValueSearch().toString());
                        doc = new Document("placa", vehiculoSearch.getPlaca());
                        vehiculoList = vehiculoRepository.findPagination(doc, page, rowPage, new Document("placa", -1));
                    } else {
                        vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    }

                    break;

                case "marca":
                    if (getValueSearch() != null) {
                        vehiculoSearch.setMarca(getValueSearch().toString());
                        vehiculoList = vehiculoRepository.findRegexInTextPagination("marca", vehiculoSearch.getMarca(), true, page, rowPage, new Document("marca", -1));

                    } else {
                        vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        vehiculoSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", vehiculoSearch.getActivo());
                        vehiculoList = vehiculoRepository.findPagination(doc, page, rowPage, new Document("placa", -1));
                    } else {
                        vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    vehiculoList = vehiculoRepository.findPagination(page, rowPage);
                    break;
            }

            vehiculoDataModel = new VehiculoDataModel(vehiculoList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearPlaca()">
    public String clearPlaca() {
        try {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca("");
            writable = false;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="findByPlaca()">
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
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
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(vehiculo.getIdvehiculo().toString(), jmoordb_user.getUsername(),
                    "update", "vehiculo", vehiculoRepository.toDocument(vehiculo).toString()));

            vehiculoRepository.update(vehiculo);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            vehiculo.setIdvehiculo(autoincrementableServices.getContador("vehiculo"));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());
        }
        return false;
    }
    // </editor-fold>

}
