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
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporte.beans.ProgramacionVehicular;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Unidad;
import com.avbravo.transporteejb.entity.Viaje;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.repository.ViajeRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.ViajeServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.PageSize;
import com.mongodb.client.model.Filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
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
public class ViajesSinConductorController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Date fechaDesde;
    private Date fechaHasta;
    //DataModel
    private ViajeDataModel viajeDataModel;

    Integer page = 1;
    Integer rowPage = 35;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Viaje viaje = new Viaje();
    Viaje viajeSelected;
    Viaje viajeSearch = new Viaje();

    //List
    List<Viaje> viajeList = new ArrayList<>();
    List<ProgramacionVehicular> programacionVehicular = new ArrayList<>();
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    SolicitudRepository solicitudRepository;
    @Inject
    ViajeRepository viajeRepository;

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
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
    public ViajesSinConductorController() {
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
            setSearchAndValue("searchViajesSinConductorController", "_betweendates");
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
            programacionVehicular = new ArrayList<>();

            this.page = page;
            viajeDataModel = new ViajeDataModel(viajeList);
            Document doc;

//            switch (getSearch()) {
//                case "_init":
//                case "_autocomplete":
//                    viajeList = viajeRepository.findPagination(page, rowPage);
//                    break;
//
//                case "activo":
//                    if (getValueSearch() != null) {
//                        viajeSearch.setActivo(getValueSearch().toString());
//                        doc = new Document("activo", viajeSearch.getActivo());
//                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("fechahorainicioreserva", -1));
//                    } else {
//                        viajeList = viajeRepository.findPagination(page, rowPage);
//                    }
//                    break;
//
//                case "_betweendates":
//            viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si",
//                    "fechahorainicioreserva", fechaDesde,
//                    "fechahorafinreserva", fechaHasta,
//                    page, rowPage, new Document("fechahorainicioreserva", -1));
            
               Bson filter = Filters.eq("activo","si");    
            viajeList = viajeRepository.filterBetweenDateWithoutHours(filter,
                    "fechahorainicioreserva", fechaDesde,
                    "fechahorafinreserva", fechaHasta,
                    new Document("fechahorainicioreserva", -1));
            
            
//                    break;
//
//                default:
//                    viajeList = viajeRepository.findPagination(page, rowPage);
//                    break;
//            }
            if (viajeList == null || viajeList.isEmpty()) {

            } else {
                for (Viaje v : viajeList) {
                    if (v.getRealizado().equals("ca") || v.getRealizado().equals("si")) {
// esta cancelado
                    } else {
                        //CONDUCTOR PENDIENTE
                        if (v.getConductor().getIdconductor() == 5) {

                            ProgramacionVehicular pv = new ProgramacionVehicular();
                            pv.setConductor(v.getConductor().getNombre());
                            pv.setFechahoraregreso(v.getFechahorafinreserva());
                            pv.setFechahorasalida(v.getFechahorainicioreserva());
                            pv.setIdviaje(v.getIdviaje());
                            pv.setMarca(v.getVehiculo().getMarca());
                            pv.setModelo(v.getVehiculo().getModelo());
                            pv.setPlaca(v.getVehiculo().getPlaca());
                            pv.setNombredia(DateUtil.nameOfDay(pv.getFechahorasalida()));
                            pv.setResponsable(v.getRealizado());
                            pv.setActivo(v.getActivo());
                            pv.setLugardestino(v.getLugardestino());
                            pv.setLugarpartida(v.getLugarpartida());
                            pv.setMision(v.getMision());
                            //Datos de la solicitud
                            pv.setFechasolicitud(v.getFechahorainicioreserva());
                            pv.setNumerosolicitudes("");

                            pv.setUnidad("");
                            pv.setResponsable("");
                            pv.setSolicita("");
                            Solicitud solicitud = new Solicitud();
//Busco la solicitud
                            Document search = new Document("viaje.idviaje", v.getIdviaje());
                            List<Solicitud> list = solicitudRepository.findBy(search);
                            if (list == null || list.isEmpty()) {

                            } else {
                                String unidad = "";

                                String numeroSolicitudes = "";
                                String responsable = "";
                                String solicita = "";
                                //Se recorren todas las solicitudes que tengan ese viaje asignado.
                                for (Solicitud s : list) {
                                    pv.setFechasolicitud(s.getFecha());
                                    for (Unidad u : s.getUnidad()) {
                                        unidad += " " + u.getIdunidad();
                                    }

                                    numeroSolicitudes += " " + String.valueOf(s.getIdsolicitud());
                                    solicita += " " + s.getUsuario().get(0).getNombre();
                                    responsable += " " + s.getUsuario().get(1).getNombre();
                                }
                                pv.setUnidad(unidad.trim());

                                pv.setNumerosolicitudes(numeroSolicitudes.trim());
                                pv.setResponsable(responsable.trim());
                                pv.setSolicita(solicita.trim());

                            }
                            programacionVehicular.add(pv);
                        }
                    }
                }//for
            }
            viajeDataModel = new ViajeDataModel(viajeList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage());

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

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("VIAJES SIN CONDUCTOR", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String texto = "Desde " + DateUtil.showDate(fechaDesde) + "  Hasta: " + DateUtil.showDate(fechaHasta);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(8);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{75, 62, 75, 82, 75, 220, 80, 105});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Partida", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Dia", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Regreso", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Unidad", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Solicitado", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Mision", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Conductor", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Vehiculo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (ProgramacionVehicular pv : programacionVehicular) {

                String fechaPartida = DateUtil.showDate(pv.getFechahorasalida()) + "             " + DateUtil.showHour(pv.getFechahorasalida());
                String fechaRegreso = DateUtil.showDate(pv.getFechahoraregreso()) + "             " + DateUtil.showHour(pv.getFechahoraregreso());
                String fechaSolicitado = DateUtil.showDate(pv.getFechasolicitud()) + "             " + DateUtil.showHour(pv.getFechasolicitud());

                table.addCell(ReportUtils.PdfCell(fechaPartida, FontFactory.getFont("arial", 10, Font.NORMAL)));

                table.addCell(ReportUtils.PdfCell(pv.getNombredia(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(fechaRegreso, FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(pv.getUnidad(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(fechaSolicitado, FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(pv.getMision(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(pv.getConductor(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(pv.getMarca() + " " + pv.getModelo() + " PLACA:" + pv.getPlaca(), FontFactory.getFont("arial", 9, Font.NORMAL)));

            }
            document.add(table);
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Boolean availablePrint()">
    public Boolean availablePrint() {
        return !programacionVehicular.isEmpty();
    }
    // </editor-fold>  

}
