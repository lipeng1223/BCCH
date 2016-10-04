package com.bc.struts.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;


import com.bc.util.ActionRole;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * This is a rip off and mod of the struts2 RolesInterceptor
 */
public class RolesInterceptor extends AbstractInterceptor {

    private static final Logger logger = Logger.getLogger(RolesInterceptor.class);

    public void destroy(){
        
    }
    
    public void init(){
        /*
        BcInvAdmin
        BcInvViewer
        BcRecAdmin
        BcRecViewer
        BcOrderAdmin
        BcOrderViewer
        BcCustomerAdmin
        BcCustomerViewer
        BcVendorAdmin
        BcVendorViewer
        BcUserAdmin
        BcUserViewer
        BellInvAdmin
        BellInvViewer
        BellRecAdmin
        BellRecViewer
        BellOrderAdmin
        BellOrderViewer
        */
        
    }
    
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String name = ServletActionContext.getActionMapping().getName();
        String method = ServletActionContext.getActionMapping().getMethod();
        
        /*
        logger.info("name: "+name);
        logger.info("namespace: "+ServletActionContext.getActionMapping().getNamespace());
        logger.info("method: "+method);
        logger.info("class: "+ServletActionContext.getActionMapping().getClass().getCanonicalName());
        logger.info("invocation action class: "+invocation.getAction().getClass().getCanonicalName());
        logger.info("invocation context name: "+invocation.getInvocationContext().getName());
        logger.info("invocation proxy class: "+invocation.getProxy().getClass().getCanonicalName());
        logger.info("invocation proxy method: "+invocation.getProxy().getMethod());
        */
        Class clazz = invocation.getAction().getClass();
        boolean allowed = false; 
        try {
            Method m = clazz.getMethod(invocation.getProxy().getMethod());
            ActionRole anno = m.getAnnotation(ActionRole.class);
            if (anno != null){
                for (String role : anno.value()){
                    if (request.isUserInRole(role)){
                        allowed = true;
                        //logger.info("User is in role: "+role);
                        break;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            logger.error("Could not get the correct method annotation", e);
        }

        String result = "403";
        if (allowed){
            result = invocation.invoke();
        }
        return result;
    }

}
