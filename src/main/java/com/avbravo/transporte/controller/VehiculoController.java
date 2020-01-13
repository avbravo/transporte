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
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.datamodel.VehiculoDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.services.TipovehiculoServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearPlaca()">
    public String clearPlaca() {
        try {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca("");
            writable = false;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
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
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            if(vehiculo.getAnio()> DateUtil.anioActual()){
              JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.aniomayorqueactual"));  
                return false;
            }
            vehiculo.setIdvehiculo(autoincrementableServices.getContador("vehiculo"));
            vehiculo.setTotalconsumo(0.0);
            vehiculo.setTotalkm(0.0);
            vehiculo.setTotalviajes(0);
            vehiculo.setEnreparacion("no");
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return false;
    }
    // </editor-fold>
   // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = vehiculoServices.isDeleted(vehiculo);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
           Boolean delete = vehiculoServices.isDeleted(vehiculo);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }
    // </editor-fold>   
    
      // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

       com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
 //       com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("VEHICULOS", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(7);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{80, 140, 140,85,85,85,85});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Placa", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Marca", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Modelo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Comnustible", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Pasajeros", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Total Km", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Viajes", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Vehiculo v : vehiculoList) {

                table.addCell(ReportUtils.PdfCell(v.getPlaca(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getMarca(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getModelo(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getCombustible(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getPasajeros().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getTotalkm().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getTotalviajes().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                
            }
            document.add(table);
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="String print">
    @Override
    public String print() {

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("VEHICULO", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));



            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Placa: " + vehiculo.getPlaca(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Marca: " + vehiculo.getMarca(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Modelo: " + vehiculo.getModelo(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Chasis: " + vehiculo.getChasis(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Tipo vehiculo: " + vehiculo.getTipovehiculo().getIdtipovehiculo(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Combustible: " + vehiculo.getCombustible(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Pasajeros: " + vehiculo.getPasajeros(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("En reparacion: " + vehiculo.getEnreparacion(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Anio: " + vehiculo.getAnio(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Km: " + vehiculo.getKm(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total Km: " + vehiculo.getTotalkm(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total Viajes: " + vehiculo.getTotalviajes(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total Consumo: " + vehiculo.getTotalconsumo(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}
