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
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
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
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.PageSize;

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
public class ProgramacionVechicularController implements Serializable, IController {

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
    public ProgramacionVechicularController() {
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
            setSearchAndValue("searchProgramacionVehicularController", "_betweendates");
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

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    viajeList = viajeRepository.findPagination(page, rowPage);
                    break;

                case "activo":
                    if (getValueSearch() != null) {
                        viajeSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", viajeSearch.getActivo());
                        viajeList = viajeRepository.findPagination(doc, page, rowPage, new Document("idviaje", -1));
                    } else {
                        viajeList = viajeRepository.findPagination(page, rowPage);
                    }
                    break;

                case "_betweendates":

                    viajeList = viajeRepository.filterBetweenDatePaginationWithoutHours("activo", "si",
                            "fechahorainicioreserva", fechaDesde,
                            "fechahorafinreserva", fechaHasta,
                            page, rowPage, new Document("idviaje", -1));
                    break;

                default:
                    viajeList = viajeRepository.findPagination(page, rowPage);
                    break;
            }
            if (viajeList == null || viajeList.isEmpty()) {

            } else {
                for (Viaje v : viajeList) {
                    ProgramacionVehicular pv = new ProgramacionVehicular();
                    pv.setConductor(v.getConductor().getNombre());
                    pv.setFechahoraregreso(v.getFechahorainicioreserva());
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

    public String columnColor(String realizado, String activo) {
        return viajeServices.columnColor(realizado, activo);
    }

    public void imprimir() {

//        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.LETTER);
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
        
              
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();

            document.add(new Paragraph(" PROGRAMACION DE FLOTA VEHICULAR \n"));

            DateFormat formatter = new SimpleDateFormat("dd/MM/yy '-' hh:mm:ss:");
            Date currentDate = new Date();
            String date = formatter.format(currentDate);
            document.add(new Paragraph("Fecha Generado: " + date));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(7);

//                  table.setTotalWidth(new float[]{ 20,72, 110, 95, 170, 72 });
            table.setTotalWidth(new float[]{20, 100,85,85,225, 72, 110});
            table.setLockedWidth(true);

            PdfPCell cell = new PdfPCell(new Paragraph("id",
                    FontFactory.getFont("arial", // fuente
                            12, // tamaño
                            Font.BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            cell.setColspan(1);
            table.addCell(cell);

          //  cell = new PdfPCell(new Paragraph("ID", FontFactory.getFont("arial", 8, Font.BOLD)));

            table.addCell("fechapartida");
            table.addCell("dia");
            table.addCell("unidad");
            table.addCell("mision");
            
            table.addCell("conductor");

            table.addCell("placa");

            for (ProgramacionVehicular pv : programacionVehicular) {
                String fechaPartida= formatter.format(pv.getFechahorasalida());
  PdfPCell cellId = new PdfPCell(new Paragraph(pv.getIdviaje().toString(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(cellId);
                PdfPCell cellFechaPartida = new PdfPCell(new Paragraph(fechaPartida, FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(cellFechaPartida);
                table.addCell(pv.getNombredia());
                table.addCell(pv.getUnidad());
               PdfPCell cellMision = new PdfPCell(new Paragraph(pv.getMision(), FontFactory.getFont("arial", 9, Font.NORMAL)));

                table.addCell(cellMision );
//                table.addCell(pv.getMision());
                table.addCell(pv.getConductor());
                table.addCell(pv.getPlaca());

            }
            document.add(table);
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
        document.close();
        FacesContext context = FacesContext.getCurrentInstance();
        Object response = context.getExternalContext().getResponse();
        if (response instanceof HttpServletResponse) {
            HttpServletResponse hsr = (HttpServletResponse) response;
            hsr.setContentType("application/pdf");
            hsr.setHeader("Contentdisposition", "attachment;filename=report.pdf");
            //        hsr.setHeader("Content-disposition", "attachment");
            hsr.setContentLength(baos.size());
            try {
                ServletOutputStream out = hsr.getOutputStream();
                baos.writeTo(out);
                out.flush();
            } catch (IOException ex) {
                System.out.println("Error:  " + ex.getMessage());
            }
            context.responseComplete();
        }
    }
}
