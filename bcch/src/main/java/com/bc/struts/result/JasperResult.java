package com.bc.struts.result;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.bc.actions.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRRtfExporter;

public class JasperResult extends StrutsResultSupport {

    private static Logger logger = Logger.getLogger(JasperResult.class);
    
    private JRAbstractBeanDataSource datasource;
    private HashMap paramMap;
    private String filename;
    private String jasperReportName; // would be something like invoice.jasper
    private String exportType;
    
    public static final String EXCEL = "excel";
    public static final String PDF = "pdf";
    public static final String RTF = "rtf";

    public void doExecute(String finalLocation, ActionInvocation invocation) throws IOException {

        // Get the HttpServletResponse
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletResponse response =
            (HttpServletResponse) actionContext.get(HTTP_RESPONSE);
        
        Object action = invocation.getAction();
        if (action instanceof BaseAction){
            BaseAction ba = (BaseAction)action;
            datasource = ba.getJasperDatasource();
            paramMap = ba.getJasperParamMap();
            filename = ba.getJasperFilename();
            jasperReportName = ba.getJasperReportName();
            exportType = ba.getJasperExportType();
        }
        
        try {
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "max-age=0");
            javax.servlet.ServletOutputStream os = response.getOutputStream();

            JasperReport report = (JasperReport)JRLoader.loadObject(this.getClass().getResourceAsStream("/WEB-INF/classes/"+jasperReportName));
            JasperPrint jp = JasperFillManager.fillReport(report, paramMap, datasource);
            if (EXCEL.equals(exportType)){
                response.setHeader("Content-disposition", "attachment; filename="+filename );
                response.setContentType("application/vnd.ms-excel;charset=windows-1252");
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            } else if (PDF.equals(exportType)){
                response.setHeader("Content-disposition", "attachment; filename="+filename );
                response.setContentType("application/pdf"); //.pdf file
                JasperExportManager.exportReportToPdfStream(jp, os);
            } else if (RTF.equals(exportType)){
                response.setHeader("Content-disposition", "attachment; filename="+filename );
                response.setContentType("application/msword"); //.doc file
                JRRtfExporter exporter = new JRRtfExporter();
                exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jp);
                exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            }

            os.flush();
            os.close();
        } catch (Throwable t){
            logger.error("Could not write back jasper report file.", t);
        }
        
    }

    public JRAbstractBeanDataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(JRAbstractBeanDataSource datasource) {
        this.datasource = datasource;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getJasperReportName() {
        return jasperReportName;
    }

    public void setJasperReportName(String jasperReportName) {
        this.jasperReportName = jasperReportName;
    }

    public HashMap getParamMap() {
        return paramMap;
    }

    public void setParamMap(HashMap paramMap) {
        this.paramMap = paramMap;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}