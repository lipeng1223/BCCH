package com.bc.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.bc.excel.ExcelConstants;

@SuppressWarnings("serial")
public class GetExportFileServlet extends HttpServlet {
    
    private static Logger logger = Logger.getLogger(GetExportFileServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("fname") != null){
            response.setContentType(ExcelConstants.CONTENT_TYPE_EXCEL);
            String fullFilename = request.getParameter("fname");
            String filename = fullFilename.substring(fullFilename.lastIndexOf("-")+1);
            response.setHeader(ExcelConstants.CONTENT_DISPOSITION_HEADER_NAME,
                               ExcelConstants.CONTENT_DISPOSITION_PFX +
                               filename + ExcelConstants.CONTENT_DISPOSITION_EXCEL_SFX);
            
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            OutputStream os = response.getOutputStream();
            Configuration config = ConfigurationManager.getConfiguration("inventory");
            String dir = config.getProperty("exportfilestore", "exportstore", "general");
            String thefile = dir+File.separator+fullFilename;
            returnFile(thefile, os);
            os.flush();
            os.close();
            
            try {
                new File(filename).delete();
                logger.info("Removed tmp export file: "+thefile);
            } catch (Throwable t){
                logger.error("Could not remove export file: "+thefile);
            }
            
        }
    }
    
    private void returnFile(String file, OutputStream out) throws FileNotFoundException, IOException {
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[4 * 1024];  // 4K char buffer
            int read;
            while ((read = bin.read(buf)) != -1) {
                out.write(buf, 0, read);
            }
        } finally {
            if (bin != null) bin.close();
        }
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
}
