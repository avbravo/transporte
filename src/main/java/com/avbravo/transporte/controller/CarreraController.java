/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.controller;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.commonejb.datamodel.CarreraDataModel;
import com.avbravo.commonejb.entity.Carrera;
import com.avbravo.commonejb.repository.CarreraRepository;
import com.avbravo.commonejb.repository.FacultadRepository;

import com.avbravo.commonejb.services.CarreraServices;
import com.avbravo.commonejb.services.FacultadServices;
import com.avbravo.jmoordb.configuration.JmoordbContext;
import com.avbravo.jmoordb.configuration.JmoordbControllerEnvironment;
import com.avbravo.jmoordb.interfaces.IController;
import com.avbravo.jmoordb.mongodb.history.repository.AutoincrementablebRepository;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
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
public class CarreraController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private CarreraDataModel carreraDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    Carrera carrera = new Carrera();
    Carrera carreraSelected;
    Carrera carreraSearch = new Carrera();

    //List
    List<Carrera> carreraList = new ArrayList<>();

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="repository">
    //Repository
    @Inject
    AutoincrementablebRepository autoincrementablebRepository;
    @Inject
    CarreraRepository carreraRepository;
    @Inject
    FacultadRepository facultadRepository;
      // </editor-fold>  
    
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    FacultadServices facultadServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    CarreraServices carreraServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return carreraRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public CarreraController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {
            autoincrementablebRepository.setDatabase("commondb");
            /*
            configurar el ambiente del contcarreraler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(carreraRepository)
                    .withEntity(carrera)
                    .withService(carreraServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("secondary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/carrera/details.jasper")
                    .withPathReportAll("/resources/reportes/carrera/all.jasper")
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
            carreraDataModel = new CarreraDataModel(carreraList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    carreraList = carreraRepository.findPagination(page, rowPage);
                    break;

                case "idcarrera":
                    if (getValueSearch() != null) {
                        carreraSearch.setIdcarrera((Integer) getValueSearch());
                        doc = new Document("idcarrera", carreraSearch.getIdcarrera());
                        carreraList = carreraRepository.findPagination(doc, page, rowPage, new Document("idcarrera", -1));
                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }

                    break;

                case "descripcion":
                    if (getValueSearch() != null) {
                        carreraSearch.setDescripcion(getValueSearch().toString());
                        //  doc = new Document("descripcion", carreraSearch.getDescripcion());
                        carreraList = carreraRepository.findRegexInTextPagination("descripcion", carreraSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        carreraSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", carreraSearch.getActivo());
                        carreraList = carreraRepository.findPagination(doc, page, rowPage, new Document("idcarrera", -1));
                    } else {
                        carreraList = carreraRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    carreraList = carreraRepository.findPagination(page, rowPage);
                    break;
            }

            carreraDataModel = new CarreraDataModel(carreraList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(),e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="beforeSave()">
    @Override
    public Boolean beforeSave() {
        try {
            carrera.setIdcarrera(autoincrementableServices.getContador("carrera"));
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
        Boolean delete = carreraServices.isDeleted(carrera);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
     Boolean delete = carreraServices.isDeleted(carrera);
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
            document.add(ReportUtils.paragraph("CARRERAS", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(3);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{45, 140, 145});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("id", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Carrera", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Facultad", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Carrera c : carreraList) {

                table.addCell(ReportUtils.PdfCell(c.getIdcarrera().toString(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getDescripcion(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(c.getFacultad().getDescripcion(), FontFactory.getFont("arial", 10, Font.NORMAL)));

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
            document.add(ReportUtils.paragraph("CARRERA", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));



            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Id: " + carrera.getIdcarrera(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Carrera: " + carrera.getDescripcion(), FontFactory.getFont("arial",12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Facultad: " + carrera.getFacultad().getDescripcion(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
   
}
