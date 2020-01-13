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
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordbutils.printer.Printer;

import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.pojos.JmoordbNotifications;
import com.avbravo.jmoordb.profiles.repository.JmoordbNotificationsRepository;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporte.beans.ProgramacionVehicular;
import com.avbravo.transporteejb.datamodel.RolDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.services.RolServices;
import com.avbravo.transporteejb.services.UsuarioServices;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
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
@Named
@ViewScoped
@Getter
@Setter
public class RolController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private RolDataModel rolDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Rol rol = new Rol();
    Rol rolSelected;
    Rol rolSearch = new Rol();

    //List
    List<Rol> rolList = new ArrayList<>();

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="reposisitory">
    //Repository
    @Inject
    RolRepository rolRepository;

    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;
    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">

    //Notification
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    JmoordbNotificationsRepository jmoordbNotificationsRepository;
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    RolServices rolServices;
    @Inject
    @Push(channel = "notification")
    private PushContext push;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return rolRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public RolController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            /*
            configurar el ambiente del controller
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(rolRepository)
                    .withEntity(rol)
                    .withService(rolServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/rol/details.jasper")
                    .withPathReportAll("/resources/reportes/rol/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();
//            String action = "gonew";
//            if (getAction() != null) {
//                action = getAction();
//            }
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean afterSave(Boolean saved)">
    @Override
    public Boolean afterSave(Boolean saved) {
        try {

            for (Usuario u : usuarioServices.getUsuarioList()) {
                //Guardarlo en la base de datos
                JmoordbNotifications jmoordbNotifications = new JmoordbNotifications();
//                Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
                jmoordbNotifications.setIdjmoordbnotifications(autoincrementableServices.getContador("jmoordbnNotifications"));
                jmoordbNotifications.setUsername(u.getUsername());
                jmoordbNotifications.setMessage("se creo un nuevo rol");
                jmoordbNotifications.setViewed("no");
                jmoordbNotifications.setDate(DateUtil.fechaActual());
                jmoordbNotifications.setType("rolnuevo");
                jmoordbNotificationsRepository.save(jmoordbNotifications);
            }
            push.send("Se creo un nuevo rol");
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }    // </editor-fold> 

// <editor-fold defaultstate="collapsed" desc="handleSelect">
    public void handleSelect(SelectEvent event) {
        try {
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="move(Integer page)">
    @Override
    public void move(Integer page) {
        try {
            this.page = page;
            rolDataModel = new RolDataModel(rolList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                    rolList = rolRepository.findPagination(page, rowPage);
                    break;
                case "_autocomplete":
                    break;

                case "idrol":
                    if (getValueSearch() != null) {
                        rolSearch.setIdrol(getValueSearch().toString());
                        doc = new Document("idrol", rolSearch.getIdrol());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }

                    break;

                case "rol":
                    if (getValueSearch() != null) {
                        rolSearch.setRol(getValueSearch().toString());
                        rolList = rolRepository.findRegexInTextPagination("rol", rolSearch.getRol(), true, page, rowPage, new Document("rol", -1));

                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        rolSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", rolSearch.getActivo());
                        rolList = rolRepository.findPagination(doc, page, rowPage, new Document("idrol", -1));
                    } else {
                        rolList = rolRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    rolList = rolRepository.findPagination(page, rowPage);
                    break;
            }

            rolDataModel = new RolDataModel(rolList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = rolServices.isDeleted(rol);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminarrol"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = rolServices.isDeleted(rol);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminarrol"));
        }
        return delete;
    }
    // </editor-fold>     

    // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String printAll() {

//        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4.rotate());
        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("ROLES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(3);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{140, 140, 45});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("idrol", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Rol", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Activo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Rol r : rolList) {

                table.addCell(ReportUtils.PdfCell(r.getIdrol(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(r.getRol(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(r.getActivo(), FontFactory.getFont("arial", 10, Font.NORMAL)));

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
    // <editor-fold defaultstate="collapsed" desc="String printAll()">
    @Override
    public String print() {

        com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            //METADATA

            document.open();
            document.add(ReportUtils.paragraph("ROLES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));



            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Idrol: " + rol.getIdrol(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Rol: " + rol.getIdrol(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Activo: " + rol.getActivo(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}
