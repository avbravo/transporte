/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.reportes;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporte.beans.ProgramacionVehicular;
import com.avbravo.transporteejb.datamodel.VehiculoDataModel;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.Vehiculo;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.VehiculoRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.VehiculoServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.io.Serializable;
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
import org.bson.conversions.Bson;
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
public class ViajesKmVehiculoController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Date fechaDesde;
    private Date fechaHasta;
    //DataModel
    private VehiculoDataModel vehiculoDataModel;

    Integer page = 1;
    Integer rowPage = 35;
    Double totalGlobalkm = 0.0;
    Double totalGlobalConsumo = 0.0;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Vehiculo vehiculoSearch = new Vehiculo();
    List<Vehiculo> vehiculoList = new ArrayList<>();
    Viaje viaje = new Viaje();
    Viaje viajeSelected;
    Viaje viajeSearch = new Viaje();

    //List
    List<Viaje> viajeList = new ArrayList<>();
   
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    VehiculoRepository vehiculoRepository;
    @Inject
    ViajeRepository viajeRepository;

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    VehiculoServices vehiculoServices;
    @Inject
    EstatusServices estatusServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    ViajeServices viajeServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    // </editor-fold>  

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return viajeRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ViajesKmVehiculoController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            fechaDesde = DateUtil.primerDiaDelMesEnFecha(DateUtil.anioActual(), DateUtil.mesActual());
            fechaHasta = DateUtil.ultimoDiaDelMesEnFecha(DateUtil.anioActual(), DateUtil.mesActual());
            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(viajeRepository)
                    .withEntity(viaje)
                    .withService(viajeServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/programacionvehicular/details.jasper")
                    .withPathReportAll("/resources/reportes/programacionvehicular/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

//            String action = "gonew";
//            if (getAction() != null) {
//                action = getAction();
//            }
//
//            if (action == null || action.equals("gonew") || action.equals("new") || action.equals("golist")) {
//                //inicializar
//
//            }
//            if (action.equals("view")) {
//                //view
//            }
         //   setSearchAndValue("_betweendates");
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
            totalGlobalkm = 0.0;
            totalGlobalConsumo = 0.0;
          
            vehiculoList = new ArrayList<>();
            List<Vehiculo> list = vehiculoRepository.findBy(new Document("activo", "si"), new Document("idvehiculo", 1));
            if (list == null || list.isEmpty()) {
                JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohayvehiculosactivos"));

            } else {
                vehiculoList = list;
                Bson filter_ = Filters.eq("realizado", "si");
                List<Viaje> listV = viajeRepository.findBy(filter_, new Document("idviaje", -1));
                if (listV == null || listV.isEmpty()) {
                    JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.nohayviajesrealizados"));
                    Integer index = 0;
                    for (Vehiculo v : vehiculoList) {
                        vehiculoList.get(index).setTotalkm(0.0);
                        vehiculoList.get(index).setTotalconsumo(0.0);
                        vehiculoList.get(index).setTotalviajes(0);
                        index++;
                    }
                } else {
                    //Recorrer los viajes de cada vehiculo en esas fechas
                    //totaliza los km y el costo de combustible en esas fechas
                    Integer index = 0;
                    for (Vehiculo v : vehiculoList) {
                        viajeList = new ArrayList<>();
                        Bson filter = Filters.and(Filters.eq("vehiculo.idvehiculo", v.getIdvehiculo()), eq("realizado", "si"),Filters.eq("activo","si"));                       
            viajeList = viajeRepository.filterBetweenDateWithoutHours(filter,
                    "fechahorainicioreserva", fechaDesde,
                    "fechahorafinreserva", fechaHasta,
                    new Document("fechahorainicioreserva", -1));
                        vehiculoList.get(index).setTotalkm(0.0);
                        vehiculoList.get(index).setTotalconsumo(0.0);
                        vehiculoList.get(index).setTotalviajes(0);
                        for (Viaje vi : viajeList) {
                            vehiculoList.get(index).setTotalkm(vehiculoList.get(index).getTotalkm() + vi.getKmestimados());
                            vehiculoList.get(index).setTotalconsumo(vehiculoList.get(index).getTotalconsumo() + vi.getCostocombustible());
                            vehiculoList.get(index).setTotalviajes(vehiculoList.get(index).getTotalviajes() + 1);
                        }
                        index++;
                    }
                }
            }

            
           
            vehiculoList.stream().map((c) -> {
                totalGlobalConsumo += c.getTotalconsumo();
                return c;
            }).forEachOrdered((c) -> {
                totalGlobalkm += c.getTotalkm();
            });
            vehiculoDataModel = new VehiculoDataModel(vehiculoList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return viajeServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return viajeServices.showHour(date);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String columnColor(String realizado, String activo)">
    public String columnColor(String realizado, String activo) {
        return viajeServices.columnColor(realizado, activo);
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

//        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("KMS RECORRIDOS POR VEHICULO", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));
            document.add(ReportUtils.paragraph("", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));
            document.add(ReportUtils.paragraph("", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));
           

            String texto = "Desde " + DateUtil.showDate(fechaDesde) + "  Hasta: " + DateUtil.showDate(fechaHasta);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(6);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{50, 85, 85, 70,70,70});

            table.setLockedWidth(true);
table.addCell(ReportUtils.PdfCell("Placa", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Marca", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Modelo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            
            table.addCell(ReportUtils.PdfCell("Km", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Viajes", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("$Consumo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
           
            for (Vehiculo v : vehiculoList) {

             
                table.addCell(ReportUtils.PdfCell(v.getPlaca(), FontFactory.getFont("arial", 10, Font.NORMAL)));

                table.addCell(ReportUtils.PdfCell(v.getMarca(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getModelo(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getTotalkm().toString(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getTotalviajes().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(v.getTotalconsumo().toString(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                

            }
            document.add(table);
            document.add(ReportUtils.paragraph("", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));
            document.add(ReportUtils.paragraph("", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));
            document.add(ReportUtils.paragraph("Total km: "+totalGlobalkm, FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(ReportUtils.paragraph("Total $: "+totalGlobalConsumo, FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_RIGHT));
           
           
            
        } catch (Exception e) {
          errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Boolean availablePrint()">
    public Boolean availablePrint() {
        return !vehiculoList.isEmpty();
    }
    // </editor-fold>  

}
