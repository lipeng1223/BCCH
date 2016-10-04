package com.bc.util;

import java.io.File;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

public class RafReader {

    private Logger logger = Logger.getLogger(RafReader.class);

    private File log;
    private Long filePos = 0L;
    private Long startedAt = 0L;
    private Long logLength = 0L;
    private Boolean finished = false;
    private byte[] buff = new byte[25000];
    private Long maxRead = 100000L;
    private Boolean completedRead = false;
    
    public RafReader(File log, Long filePos){
        this.log = log;
        this.filePos = filePos;
        startedAt = filePos;
        logLength = log.length();
        //logger.info("RafReader created to "+log.getAbsolutePath());
        //logger.info("Log length: "+logLength);
    }
    
    public Boolean finished(){
        return finished;
    }
    
    public String content(){
        //logger.error("filePos start: "+filePos+" log length: "+logLength);
        if (filePos - startedAt > maxRead){
            finished = true;            
        }
        int bytesRead = 0;
        if (filePos < logLength && !finished){
            try {
                RandomAccessFile raf = new RandomAccessFile(log, "r");
                raf.seek(filePos);
                int len = buff.length;
                if (filePos + buff.length > logLength){
                    len = new Long(logLength - filePos).intValue();
                }
                bytesRead = raf.read(buff, 0, len);
                if (bytesRead > -1){
                    filePos += bytesRead;
                    if (filePos >= logLength) {
                        completedRead = true;
                        finished = true;
                    }
                } else {
                    finished = true;
                    filePos = logLength;
                }
                raf.close();
            } catch (Exception e){
                logger.error("Could not read from file", e);
            }
        } else {
            completedRead = true;
            finished = true;
        }
        
        //logger.error("filePos end: "+filePos+" log length: "+logLength+" bytes read: "+bytesRead);
        
        //String chunk = new String(buff, 0, bytesRead);
        //logger.error("filePos end: "+filePos+" log length: "+logLength+" bytes read: "+bytesRead+" chunk length: "+chunk.length());
        
        return new String(buff, 0, bytesRead);
    }
    
    public void cleanup(){
        buff = null;
    }

    public File getLog() {
        return log;
    }

    public void setLog(File log) {
        this.log = log;
    }

    public Long getFilePos() {
        return filePos;
    }

    public void setFilePos(Long filePos) {
        this.filePos = filePos;
    }

    public Boolean getCompletedRead() {
        return completedRead;
    }

    public void setCompletedRead(Boolean completedRead) {
        this.completedRead = completedRead;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getLogLength() {
        return logLength;
    }

    public void setLogLength(Long logLength) {
        this.logLength = logLength;
    }
    
}
