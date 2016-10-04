package com.bc.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.bc.util.ActionRole;
import com.opensymphony.xwork2.ActionSupport;

@Namespace("")
    @Result(name="homeredirect", location="/secure/home.bc", type="redirect")
public class LogoffAction extends ActionSupport implements ServletRequestAware {

    private HttpServletRequest request;
    
    @Override
    @ActionRole({"WebUser"})
    public String execute(){
        request.getSession().invalidate();
        return "homeredirect";
    }
    
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
    
}
