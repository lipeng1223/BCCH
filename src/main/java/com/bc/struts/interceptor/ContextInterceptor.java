package com.bc.struts.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.bc.util.ThreadContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

@SuppressWarnings("serial")
public class ContextInterceptor extends AbstractInterceptor {

    private static final Logger logger = Logger.getLogger(ContextInterceptor.class);

    public void destroy(){
        
    }
    
    public void init(){
        
    }
    
    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            String method = ServletActionContext.getActionMapping().getMethod();
            Object action = invocation.getAction();
            StringBuilder userAction = new StringBuilder();
            userAction.append(action.getClass().getSimpleName());
            userAction.append(" ");
            userAction.append(method);
            Long userId = (Long)request.getSession().getAttribute("userId");
            String username = (String)request.getSession().getAttribute("username");
            ThreadContext.setContext(userId, username, userAction.toString());
        } catch (Exception e){
            logger.error("Could not setup the ThreadContext", e);
        }
        
        return invocation.invoke();
    }

}
