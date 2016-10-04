package com.bc.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.util.ActionRole;
import com.bc.util.RafReader;

@ParentPackage("bcpackage")
@Namespace("/secure")
@Results({
    @Result(name="logviewtext", location="/WEB-INF/jsp/system/logviewtext.jsp"),
    @Result(name="logview", location="/WEB-INF/jsp/system/logview.jsp")
})
public class LogViewAction extends BaseAction {

    private Logger logger = Logger.getLogger(LogViewAction.class);
    
    private List<String> availableLogFiles;
    private String logToView;
    private Long filePos = 0L;
    
    @ActionRole({"SystemAdmin"})
    public String execute(){
        try {
            availableLogFiles = new ArrayList<String>();
            File logDir = new File(getConfigProperty("serverlogdir", ""));
            if (logDir.exists()){
                //logger.info("getting log files");
                Collection<File> files = FileUtils.listFiles(logDir, null, false);
                for (File f : files){
                    //logger.info("added file name: "+f.getName());
                    availableLogFiles.add(f.getName());
                }
            }
        } catch (Exception e){
            logger.error("Could not get the files in the logdir", e);
        }
        return "logview";
    }
    
    @ActionRole({"SystemAdmin"})
    public String view(){
        try {
            File log = new File(getConfigProperty("serverlogdir", "")+File.separator+logToView);
            RafReader rafReader = new RafReader(log, filePos);
            ServletActionContext.getRequest().setAttribute("rafReader", rafReader);
        } catch (Exception e){
            logger.error("Could not view file: "+logToView, e);
        }
        return "logviewtext";
    }

    public List<String> getAvailableLogFiles() {
        return availableLogFiles;
    }

    public void setAvailableLogFiles(List<String> availableLogFiles) {
        this.availableLogFiles = availableLogFiles;
    }

    public String getLogToView() {
        return logToView;
    }

    public void setLogToView(String logToView) {
        this.logToView = logToView;
    }

    public Long getFilePos() {
        return filePos;
    }

    public void setFilePos(Long filePos) {
        this.filePos = filePos;
    }

}
