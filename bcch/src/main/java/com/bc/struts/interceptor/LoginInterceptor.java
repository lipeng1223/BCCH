package com.bc.struts.interceptor;

import java.util.HashSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.bc.ejb.UserSession;
import com.bc.ejb.UserSessionLocal;
import com.bc.orm.User;
import com.bc.orm.UserRole;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

@SuppressWarnings("serial")
public class LoginInterceptor extends AbstractInterceptor {

    private static final Logger logger = Logger.getLogger(LoginInterceptor.class);

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        /*
         * This is so we can remove bad input data on field input and trim input strings
        if (request.getParameter("ignoreKill") == null || !request.getParameter("ignoreKill").equals("true")){
            invocation.getInvocationContext().setParameters(KillStringInputScrubber.killParameterMap(invocation.getInvocationContext().getParameters()));
        }
         */

        HttpSession session = request.getSession();
        if (session != null && session.getAttribute("userId") == null){
            try {
                Context ctx =   new InitialContext();
                UserSessionLocal userSession = (UserSessionLocal)ctx.lookup(UserSession.LocalJNDIString);
                User user = userSession.findByName(request.getUserPrincipal().getName(), "roles");
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                
                HashSet<String> roles = new HashSet<String>();
                for (UserRole ur : user.getRoles()){
                    roles.add(ur.getRole());
                }
                session.setAttribute("roles", roles);
                
            } catch (Exception e){
                logger.error("Could not setup session data", e);
            }
        }
        
        return invocation.invoke();
    }

}
