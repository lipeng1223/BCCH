package com.bc.util;

import java.io.Serializable;
import java.util.Date;

import com.bc.orm.Audit;

@SuppressWarnings("serial")
public class AuditMessage implements Serializable {

    private Audit audit;
    private Date time;
    private String username;
    private Long userId;
    
    
    
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Audit getAudit() {
        return audit;
    }
    public void setAudit(Audit audit) {
        this.audit = audit;
    }
    
}
