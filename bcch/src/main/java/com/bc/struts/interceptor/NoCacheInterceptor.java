package com.bc.struts.interceptor;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class NoCacheInterceptor extends AbstractInterceptor implements Serializable {
	
    private static final Logger logger = Logger.getLogger(NoCacheInterceptor.class);

    public void destroy(){
        
    }
    
    public void init(){
        
    }

    public String intercept(ActionInvocation invocation) throws Exception {

		ActionContext context = invocation.getInvocationContext();

		HttpServletResponse response = ServletActionContext.getResponse();
		if(response!=null) {
			response.setHeader("Cache-control","no-cache, no-store");
			response.setHeader("Pragma","no-cache");
			response.setHeader("Expires","-1");
		}

		return invocation.invoke();
	}
}
