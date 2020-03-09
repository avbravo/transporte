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
import com.avbravo.transporteejb.datamodel.SolicitudDataModel;
import com.avbravo.transporteejb.datamodel.ViajeDataModel;
import com.avbravo.transporteejb.entity.Solicitud;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.entity.VistoBueno;
import com.avbravo.transporteejb.repository.SolicitudRepository;
import com.avbravo.transporteejb.services.EstatusServices;
import com.avbravo.transporteejb.services.SolicitudServices;
import com.avbravo.transporteejb.services.VistoBuenoServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;
import com.mongodb.client.model.Filters;

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
public class SolicitudesPendientesController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private Date fechaDesde;
    private Date fechaHasta;
    //DataModel
    private ViajeDataModel viajeDataModel;
    private SolicitudDataModel solicitudDataModel;

    Integer page = 1;
    Integer rowPage = 35;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Solicitud solicitudSearch = new Solicitud();
    Solicitud solicitud = new Solicitud();

    //List
    List<Solicitud> solicitudList = new ArrayList<>();
    List<Solicitud> solicitudListSelected = new ArrayList<>();

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="repository">
    //Repository
    @Inject
    SolicitudRepository solicitudRepository;

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
    VistoBuenoServices vistoBuenoServices;
    @Inject
    SolicitudServices solicitudServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    // </editor-fold>  

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return solicitudRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SolicitudesPendientesController() {
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
                    .withRepository(solicitudRepository)
                    .withEntity(solicitud)
                    .withService(solicitudServices)
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

            solicitudList = new ArrayList<>();
            this.page = page;
            solicitudDataModel = new SolicitudDataModel(solicitudList);
            Document doc;
            
         Bson filter = Filters.eq("estatus.idestatus", "SOLICITADO");
          solicitudList = solicitudRepository.filterBetweenDateWithoutHours(filter,
                    "fechahorapartida", fechaDesde,
                    "fechahoraregreso", fechaHasta,
                    new Document("fechahorapartida", -1));

           

            solicitudDataModel = new SolicitudDataModel(solicitudList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String showDate(Date date)">
    public String showDate(Date date) {
        return solicitudServices.showDate(date);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="String showHour(Date date)">

    public String showHour(Date date) {
        return solicitudServices.showHour(date);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.LETTER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("SOLICITUDES PENDIENTES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String texto = "Desde " + DateUtil.showDate(fechaDesde) + "  Hasta: " + DateUtil.showDate(fechaHasta);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(5);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{62, 75, 75, 90,90});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("#", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Fecha Partida", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Fecha Regreso", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Solicita", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Responsable", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Solicitud s : solicitudList) {

                String fechaPartida = DateUtil.showDate(s.getFechahorapartida()) + "             " + DateUtil.showHour(s.getFechahorapartida());
                String fechaRegreso = DateUtil.showDate(s.getFechahoraregreso()) + "             " + DateUtil.showHour(s.getFechahoraregreso());
                String fechaSolicitado = DateUtil.showDate(s.getFecha()) + "             " + DateUtil.showHour(s.getFecha());

                table.addCell(ReportUtils.PdfCell(s.getIdsolicitud().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(fechaPartida, FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(fechaRegreso, FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(s.getUsuario().get(0).getNombre(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(s.getUsuario().get(1).getNombre(), FontFactory.getFont("arial", 9, Font.NORMAL)));

            }
            document.add(table);
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
        return !solicitudList.isEmpty();
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="String columnNameVistoBueno(VistoBueno vistoBueno) ">
    public String columnNameVistoBueno(VistoBueno vistoBueno) {
        return vistoBuenoServices.columnNameVistoBueno(vistoBueno);
    }
// </editor-fold>
}
