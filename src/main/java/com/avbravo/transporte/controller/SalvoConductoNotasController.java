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
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordb.mongodb.history.services.AutoincrementableServices;
import com.avbravo.jmoordbutils.DateUtil;

import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.datamodel.SalvoConductoNotasDataModel;
import com.avbravo.transporteejb.entity.SalvoConductoNotas;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.SalvoConductoNotasRepository;
import com.avbravo.transporteejb.services.SalvoConductoNotasServices;
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
public class SalvoConductoNotasController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    //DataModel
    private SalvoConductoNotasDataModel salvoConductoNotasDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages = new ArrayList<>();

    //Entity
    SalvoConductoNotas salvoConductoNotas = new SalvoConductoNotas();
    SalvoConductoNotas salvoConductoNotasSelected;
    SalvoConductoNotas salvoConductoNotasSearch = new SalvoConductoNotas();

    //List
    List<SalvoConductoNotas> salvoConductoNotasList = new ArrayList<>();
// </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="repository">

    //Repository
    @Inject
    SalvoConductoNotasRepository salvoConductoNotasRepository;
    // </editor-fold>  
// <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    SalvoConductoNotasServices salvoConductoNotasServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return salvoConductoNotasRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public SalvoConductoNotasController() {
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
                    .withRepository(salvoConductoNotasRepository)
                    .withEntity(salvoConductoNotas)
                    .withService(salvoConductoNotasServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(false)
                    .withPathReportDetail("/resources/reportes/salvoconductonotas/details.jasper")
                    .withPathReportAll("/resources/reportes/salvoconductonotas/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
    }// </editor-fold>

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
            salvoConductoNotasDataModel = new SalvoConductoNotasDataModel(salvoConductoNotasList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    salvoConductoNotasList = salvoConductoNotasRepository.findPagination(page, rowPage);
                    break;

                case "idsalvoconductonotas":
                    if (getValueSearch() != null) {
                        salvoConductoNotasSearch.setIdsalvoconductonotas(getValueSearch().toString());
                        doc = new Document("idsalvoconductonotas", salvoConductoNotasSearch.getIdsalvoconductonotas());
                        salvoConductoNotasList = salvoConductoNotasRepository.findPagination(doc, page, rowPage, new Document("idsalvoConductoNotas", -1));
                    } else {
                        salvoConductoNotasList = salvoConductoNotasRepository.findPagination(page, rowPage);
                    }

                    break;

                case "descripcion":
                    if (getValueSearch() != null) {
                        salvoConductoNotasSearch.setDescripcion(getValueSearch().toString());
                        System.out.println("Description:==>>>> " + salvoConductoNotasSearch.getDescripcion());
                        salvoConductoNotasList = salvoConductoNotasRepository.findRegexInTextPagination("descripcion", salvoConductoNotasSearch.getDescripcion(), true, page, rowPage, new Document("descripcion", -1));

                    } else {
                        salvoConductoNotasList = salvoConductoNotasRepository.findPagination(page, rowPage);
                    }
                case "activo":
                    if (getValueSearch() != null) {
                        salvoConductoNotasSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", salvoConductoNotasSearch.getActivo());
                        salvoConductoNotasList = salvoConductoNotasRepository.findPagination(doc, page, rowPage, new Document("idsalvoConductoNotas", -1));
                    } else {
                        salvoConductoNotasList = salvoConductoNotasRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    salvoConductoNotasList = salvoConductoNotasRepository.findPagination(page, rowPage);
                    break;
            }

            salvoConductoNotasDataModel = new SalvoConductoNotasDataModel(salvoConductoNotasList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = salvoConductoNotasServices.isDeleted(salvoConductoNotas);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = salvoConductoNotasServices.isDeleted(salvoConductoNotas);
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
            document.add(ReportUtils.paragraph("SalvoConductoNotas ", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(2);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{140, 35});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Id", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            table.addCell(ReportUtils.PdfCell("Activo", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (SalvoConductoNotas l : salvoConductoNotasList) {

                table.addCell(ReportUtils.PdfCell(l.getIdsalvoconductonotas(), FontFactory.getFont("arial", 10, Font.NORMAL)));

                table.addCell(ReportUtils.PdfCell(l.getActivo(), FontFactory.getFont("arial", 10, Font.NORMAL)));

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
            document.add(ReportUtils.paragraph("SalvoConductoNotas", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Id: " + salvoConductoNotas.getIdsalvoconductonotas(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

            document.add(ReportUtils.paragraph("Activo: " + salvoConductoNotas.getActivo(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>  
}
