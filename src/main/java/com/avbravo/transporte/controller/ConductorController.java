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
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.repository.RevisionHistoryRepository;
import com.avbravo.jmoordb.services.RevisionHistoryServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.datamodel.ConductorDataModel;
import com.avbravo.transporteejb.entity.Conductor;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.ConductorRepository;
import com.avbravo.transporteejb.services.ConductorServices;
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
public class ConductorController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private ConductorDataModel conductorDataModel;
    private String cedulanueva = "";

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Conductor conductor = new Conductor();
    Conductor conductorSelected;
    Conductor conductorSearch = new Conductor();

    //List
    List<Conductor> conductorList = new ArrayList<>();
    List<Conductor> conductorListSelected = new ArrayList<>();
// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    ConductorRepository conductorRepository;
    @Inject
    RevisionHistoryRepository revisionHistoryRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    ConductorServices conductorServices;
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

        return conductorRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public ConductorController() {
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
                    .withRepository(conductorRepository)
                    .withEntity(conductor)
                    .withService(conductorServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/conductor/details.jasper")
                    .withPathReportAll("/resources/reportes/conductor/all.jasper")
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
            conductorDataModel = new ConductorDataModel(conductorList);
            Document doc;

        
            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    conductorList = conductorRepository.findPagination(page, rowPage);
                    break;

                case "idconductor":
                    if (getValueSearch() != null) {
                        conductorSearch.setIdconductor(Integer.parseInt(getValueSearch().toString()));
                        doc = new Document("idconductor", conductorSearch.getIdconductor());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
                case "cedula":
                    if (getValueSearch() != null) {
                        conductorSearch.setCedula(getValueSearch().toString());
                        doc = new Document("cedula", conductorSearch.getCedula());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
             
                case "nombre":
                    if (getValueSearch() != null) {
                        conductorSearch.setNombre(getValueSearch().toString());
                        conductorList = conductorRepository.findRegexInTextPagination("nombre", conductorSearch.getNombre(), true, page, rowPage, new Document("nombre", -1));

                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        conductorSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", conductorSearch.getActivo());
                        conductorList = conductorRepository.findPagination(doc, page, rowPage, new Document("cedula", -1));
                    } else {
                        conductorList = conductorRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    conductorList = conductorRepository.findPagination(page, rowPage);
                    break;
            }

            conductorDataModel = new ConductorDataModel(conductorList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="clearCedula()">
    public String clearCedula() {
        try {
            conductor = new Conductor();
            conductor.setCedula("");
            writable = false;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="findByCedula()">
    public String findByCedula() {
        try {
             if(conductor == null || conductor.getCedula()== null||conductor.getCedula().equals("")){
              JsfUtil.warningDialog(rf.getAppMessage("warning.view"), rf.getMessage("warning.ingresecedula"));
                return "";
            }
            if (JsfUtil.isVacio(conductor.getCedula())) {
                writable = false;
                return "";
            }
            conductor.setCedula(conductor.getCedula().toUpperCase());
            writable = true;
            List<Conductor> list = conductorRepository.findBy(new Document("cedula", conductor.getCedula()));
            if (list.isEmpty()) {
                writable = false;

                JsfUtil.warningMessage(rf.getAppMessage("warning.idnotexist"));
                return "";
            } else {
                writable = true;
                conductor = list.get(0);

                conductorSelected = conductor;
            }

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }

        return "";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="editCedula">
    public String editCedula() {
        try {

            if (conductor.getCedula().equals(cedulanueva)) {
                JsfUtil.warningMessage(rf.getMessage("warning.cedulasiguales"));
                return "";
            }

            Integer c = conductorRepository.count(new Document("cedula", cedulanueva));
            if (c >= 1) {
                JsfUtil.warningMessage(rf.getMessage("warning.conductorceduladuplicada"));
                return "";
            }

            conductor.setCedula(cedulanueva);
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            revisionHistoryRepository.save(revisionHistoryServices.getRevisionHistory(conductor.getIdconductor().toString(), jmoordb_user.getUsername(),
                    "update", "conductor", conductorRepository.toDocument(conductor).toString()));

            conductorRepository.update(conductor);
            JsfUtil.successMessage(rf.getAppMessage("info.update"));
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);
        }
        return "";
    }// </editor-fold>
    
    
     // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    public Boolean beforeSave() {
        try {
            conductor.setIdconductor(autoincrementableServices.getContador("conductor"));
            conductor.setTotalconsumo(0.0);
            conductor.setTotalkm(0.0);
            conductor.setTotalviajes(0);
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
        Boolean delete = conductorServices.isDeleted(conductor);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
   Boolean delete = conductorServices.isDeleted(conductor);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
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
            document.add(ReportUtils.paragraph("CONDUCTORES", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(6);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{65, 140, 45,20,65,65});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Cedula", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Nombre", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Celular", FontFactory.getFont("arial", 10, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Control", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Km", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Viajes", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            

            for (Conductor c: conductorList) {

                table.addCell(ReportUtils.PdfCell(c.getCedula(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getNombre(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getCelular(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                
                table.addCell(ReportUtils.PdfCell(c.getEscontrol(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getTotalkm().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getTotalviajes().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                
                

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
            document.add(ReportUtils.paragraph("CONDUCTOR", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));



            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Cedula: " + conductor.getCedula(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Nombre: " + conductor.getNombre(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Celular: " + conductor.getCelular(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Email: " + conductor.getEmail(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Control: " + conductor.getEscontrol(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total km: " + conductor.getTotalkm(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total viajes: " + conductor.getTotalviajes(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Total consumo: " + conductor.getTotalconsumo(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}
