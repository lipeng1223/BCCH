package com.bc.orm;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.log4j.Logger;
import org.jboss.security.SecurityAssociation;

public class BaseEntityListener {
    private static Logger logger = Logger.getLogger(BaseEntityListener.class);

    @PrePersist
    public void setCreateInfo(BaseEntity be) {
        if (be.getCreateTime() == null) {
            be.setCreateTime(new Date());
        }
        //if (be.getCreatedBy() == null){
        //    be.setCreatedBy(getUsername());
        //}
        setLastUpdateInfo(be);
    }
    
    @PreUpdate
    public void setLastUpdateInfo(BaseEntity be) {
        be.setLastUpdate(new Date());
        be.setLastUpdateBy(getUsername());
    }
    
    /*
     * Get the username via calling principal for now.       
     * This is jboss specific and won't work if we decide to
     * run our ejbs as something other than the caller.
     */
    private String getUsername() {
        String uname = "";
        try {
            if (SecurityAssociation.getCallerPrincipal() != null){
                uname = SecurityAssociation.getCallerPrincipal().getName();
            }
        } catch (Throwable t) {
            logger.warn("Error getting Username:" + t);
        }
        return uname;
    }
    
}
