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
import com.avbravo.jmoordb.mongodb.history.services.ErrorInfoServices;
import com.avbravo.jmoordbutils.DateUtil;
import com.avbravo.jmoordbutils.JsfUtil;
import com.avbravo.jmoordbutils.printer.Printer;
import com.avbravo.jmoordbutils.JmoordbResourcesFiles;
import com.avbravo.jmoordbutils.ReportUtils;
import com.avbravo.transporteejb.datamodel.UsuarioDataModel;
import com.avbravo.transporteejb.entity.Rol;
import com.avbravo.transporteejb.entity.Usuario;
import com.avbravo.transporteejb.repository.RolRepository;
import com.avbravo.transporteejb.repository.UsuarioRepository;
import com.avbravo.transporteejb.services.RolServices;
import com.avbravo.transporteejb.services.UnidadServices;
import com.avbravo.transporteejb.services.UsuarioServices;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
public class UsuarioController implements Serializable, IController {

// <editor-fold defaultstate="collapsed" desc="fields">  
    private static final long serialVersionUID = 1L;

    private Boolean writable = false;
    private String passwordnewrepeat;
    //DataModel
    private UsuarioDataModel usuarioDataModel;

    Integer page = 1;
    Integer rowPage = 25;
    List<Integer> pages9 = new ArrayList<>();

    //Entity
    Usuario usuario = new Usuario();
    Usuario usuarioSelected;
    Usuario usuarioSearch = new Usuario();

    //List
    List<Usuario> usuarioList = new ArrayList<>();
    List<Usuario> usuarioListSelected = new ArrayList<>();
    //Para multiples roles
    List<Rol> rolList = new ArrayList();

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="repository">
    //Repository
    @Inject
    RolRepository rolRepository;
    @Inject
    UsuarioRepository usuarioRepository;

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="services">
    //Services
    @Inject
    AutoincrementableServices autoincrementableServices;
    @Inject
    ErrorInfoServices errorServices;
    @Inject
    RolServices rolServices;
    @Inject
    UnidadServices unidadServices;
    @Inject
    UsuarioServices usuarioServices;
    @Inject
    JmoordbResourcesFiles rf;
    @Inject
    Printer printer;

    //List of Relations
    //Repository of Relations
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="getter/setter">
    public List<Integer> getPages() {

        return usuarioRepository.listOfPage(rowPage);
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="constructor">
    public UsuarioController() {
    }

    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="init">
    @PostConstruct
    public void init() {
        try {

            /*
            configurar el ambiente del contusuarioler
             */
            HashMap parameters = new HashMap();
            Usuario jmoordb_user = (Usuario) JmoordbContext.get("jmoordb_user");
            //    parameters.put("P_EMPRESA", jmoordb_user.getEmpresa().getDescripcion());

            JmoordbControllerEnvironment jmc = new JmoordbControllerEnvironment.Builder()
                    .withController(this)
                    .withRepository(usuarioRepository)
                    .withEntity(usuario)
                    .withService(usuarioServices)
                    .withNameFieldOfPage("page")
                    .withNameFieldOfRowPage("rowPage")
                    .withTypeKey("primary")
                    .withSearchLowerCase(true)
                    .withPathReportDetail("/resources/reportes/usuario/details.jasper")
                    .withPathReportAll("/resources/reportes/usuario/all.jasper")
                    .withparameters(parameters)
                    .withResetInSave(true)
                    .withAction("golist")
                    .build();

            start();
            //En este caso desencriptamos el password
            String action = getAction();
            if (action.equals("view")) {
                usuario.setPassword(JsfUtil.desencriptar(usuario.getPassword()));
                rolList = usuario.getRol();
                usuarioSelected = usuario;
            }

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
            usuarioDataModel = new UsuarioDataModel(usuarioList);
            Document doc;

            switch (getSearch()) {
                case "_init":
                case "_autocomplete":
                    usuarioList = usuarioRepository.findPagination(page, rowPage);
                    break;

                case "username":
                    if (getValueSearch() != null) {
                        usuarioSearch.setUsername(getValueSearch().toString());
                        doc = new Document("username", usuarioSearch.getUsername());
                        usuarioList = usuarioRepository.findPagination(doc, page, rowPage, new Document("idusuario", -1));
                    } else {
                        usuarioList = usuarioRepository.findPagination(page, rowPage);
                    }

                    break;
                case "activo":
                    if (getValueSearch() != null) {
                        usuarioSearch.setActivo(getValueSearch().toString());
                        doc = new Document("activo", usuarioSearch.getActivo());
                        usuarioList = usuarioRepository.findPagination(doc, page, rowPage, new Document("idusuario", -1));
                    } else {
                        usuarioList = usuarioRepository.findPagination(page, rowPage);
                    }
                    break;

                default:
                    usuarioList = usuarioRepository.findPagination(page, rowPage);
                    break;
            }

            usuarioDataModel = new UsuarioDataModel(usuarioList);

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);

        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Boolean beforeSave()">
    public Boolean beforeSave() {
        try {
            //password nuevo no coincide
            if (!usuario.getPassword().equals(passwordnewrepeat)) {
                JsfUtil.warningMessage(rf.getMessage("warning.passwordnocoinciden"));
                return false;
            }
            
            usuario.setRol(rolList);
            usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeEdit()">
    public Boolean beforeEdit() {
        try {
            usuario.setRol(rolList);
            usuario.setPassword(JsfUtil.encriptar(usuario.getPassword()));
            return true;
        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDelete()">
    @Override
    public Boolean beforeDelete() {
        Boolean delete = usuarioServices.isDeleted(usuario);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="Boolean beforeDeleteFromListXhtml()">
    @Override
    public Boolean beforeDeleteFromListXhtml() {
        Boolean delete = usuarioServices.isDeleted(usuario);
        if (!delete) {
            JsfUtil.warningDialog(rf.getMessage("warning.advertencia"), rf.getMessage("warning.nosepuedeeliminar"));
        }
        return delete;
    }

    // </editor-fold>   
    // <editor-fold defaultstate="collapsed" desc="completeFiltrado(String query)">
    /**
     * Se usa para los autocomplete filtrando
     *
     * @param query
     * @return
     */
    public List<Rol> completeFiltrado(String query) {
        List<Rol> suggestions = new ArrayList<>();
        List<Rol> temp = new ArrayList<>();
        try {
            Boolean found = false;
            query = query.trim();
            String field = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("field");
            temp = rolRepository.findRegex(field, query, true, new Document(field, 1));

            if (rolList.isEmpty()) {
                if (!temp.isEmpty()) {
                    suggestions = temp;
                }
            } else {
                if (!temp.isEmpty()) {

                    for (Rol r : temp) {
                        found = false;
                        for (Rol r2 : rolList) {
                            if (r.getIdrol().equals(r2.getIdrol())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            suggestions.add(r);
                        }

                    }
                }

            }
            //suggestions=  rolRepository.findRegex(field,query,true,new Document(field,1));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        return suggestions;
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
            document.add(ReportUtils.paragraph("USUARIO", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "Fecha " + DateUtil.showDate(currentDate);
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            //Numero de columnas
            PdfPTable table = new PdfPTable(4);

//Aqui indicamos el tamaño de cada columna
            table.setTotalWidth(new float[]{140, 140, 140, 140});

            table.setLockedWidth(true);

            table.addCell(ReportUtils.PdfCell("Username", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Nombre", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Unidad", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));
            table.addCell(ReportUtils.PdfCell("Roles", FontFactory.getFont("arial", 11, Font.BOLD), Element.ALIGN_CENTER));

            for (Usuario u : usuarioList) {
                String rol = "";
                rol = u.getRol().stream().map((r) -> r.getRol() + " ").reduce(rol, String::concat);

                table.addCell(ReportUtils.PdfCell(u.getUsername(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(u.getNombre(), FontFactory.getFont("arial", 9, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(u.getUnidad().getIdunidad(), FontFactory.getFont("arial", 10, Font.NORMAL)));
                table.addCell(ReportUtils.PdfCell(rol, FontFactory.getFont("arial", 10, Font.NORMAL)));

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
            document.add(ReportUtils.paragraph("USUARIO", FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            Date currentDate = new Date();
            String texto = "REPORTE";
            document.add(ReportUtils.paragraph(texto, FontFactory.getFont("arial", 12, Font.BOLD), Element.ALIGN_CENTER));

            String date = DateUtil.showDate(currentDate) + " " + DateUtil.showHour(currentDate);

            document.add(ReportUtils.paragraph("Fecha: " + date, FontFactory.getFont("arial", 8, Font.BOLD), Element.ALIGN_RIGHT));
            document.add(new Paragraph("\n"));

            document.add(ReportUtils.paragraph("Username: " + usuario.getUsername(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Nombre: " + usuario.getNombre(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Cedula: " + usuario.getCedula(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Email: " + usuario.getEmail(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Cargo: " + usuario.getCargo(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
            document.add(ReportUtils.paragraph("Unidad: " + usuario.getUnidad(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
             String rol = "";
                rol = usuario.getRol().stream().map((r) -> r.getRol() + " " ).reduce(rol, String::concat);
            document.add(ReportUtils.paragraph("Rol: " + rol, FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));
 

            document.add(ReportUtils.paragraph("Activo: " + usuario.getActivo(), FontFactory.getFont("arial", 12, Font.NORMAL), Element.ALIGN_JUSTIFIED));

        } catch (Exception e) {
            errorServices.errorMessage(nameOfClass(), nameOfMethod(), e.getLocalizedMessage(), e);
        }
        document.close();

        ReportUtils.printPDF(baos);
        return "";
    }
    // </editor-fold>   
}
