package com.bc.struts.result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.bc.excel.ExcelReportExporter;
import com.bc.actions.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * Custom Result that returns an Excel spreadsheet.
 *
 */
public class ExcelFileResult extends StrutsResultSupport {

    private static Logger logger = Logger.getLogger(ExcelFileResult.class);

    /** Name of the Action property which is an instance of ExcelExportInfo. */
    private String excelDataName = "excelReportExporter";

    /** Contains the info used to generate the Excel sheet */
    private ExcelReportExporter exporter;


    public void doExecute(String finalLocation, ActionInvocation invocation) throws IOException {

        Object action = invocation.getAction();
        String fileName = "InventoryExport";
        String sheetName = "Export";
        HashMap<String, List> sheetMap = null;
        if (action instanceof BaseAction){
            logger.info("Action is instance of BaseAction");
            BaseAction ba = (BaseAction)action;
            fileName = ba.getExcelExportFileName().replace("/", "-").replace(" ", "-");
            logger.info("Action Excel Export File Name : " + ba.getExcelExportFileName());
            logger.info("File Name : " + fileName);
            sheetName = ba.getExcelExportSheetName();
            sheetMap = ba.getExcelReportExporter().getReport().getResultsMap();
            if (ba.getExportLimitExceeded()){
                logger.info("ExportLimitExceeded");
                ActionContext actionContext = invocation.getInvocationContext();
                HttpServletResponse response =
                    (HttpServletResponse) actionContext.get(HTTP_RESPONSE);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                StringBuilder json = new StringBuilder("{success:false,exportLimitExceeded:true}");
                OutputStream os = response.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                return;
            }
        }

        // Get the excel info
        exporter = (ExcelReportExporter) invocation.getStack().findValue(conditionalParse(excelDataName, invocation));

        //exporter.configureResponse(response, fileName);
        
        String fname = System.currentTimeMillis()+"-"+fileName;
        Configuration config = ConfigurationManager.getConfiguration("inventory");
        String dir = config.getProperty("exportfilestore", "exportstore", "general");
        String filename = dir+File.separator+fname;
        logger.info("Writing tmp export file to: "+filename);
        FileOutputStream fos = new FileOutputStream(filename);
        if (sheetMap != null) {
            List<String> sheetNames = new ArrayList<String>();
            sheetNames.addAll(sheetMap.keySet());
            exporter.writeOutputStream(fos, sheetNames);
        }
        else {
            exporter.writeOutputStream(fos, sheetName);
        }
        logger.info("Finished Writing file");
        
        // write back the status json
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletResponse response =
            (HttpServletResponse) actionContext.get(HTTP_RESPONSE);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        StringBuilder json = new StringBuilder("{success: true,filename:'");
        json.append(fname);
        json.append("'}");
        OutputStream os = response.getOutputStream();
        os.write(json.toString().getBytes());
        os.flush();
    }

    /**
     * Get the excelDataName.
     *
     * @return The value of excelDataName.
     */
    public String getExcelDataName() {
        return excelDataName;
    }

    /**
     * Set the excelDataName.
     *
     * @param excelDataName
     */
    public void setExcelDataName(String excelDataName) {
        this.excelDataName = excelDataName;
    }

}