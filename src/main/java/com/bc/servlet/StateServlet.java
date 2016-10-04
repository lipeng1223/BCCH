package com.bc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bc.util.StateManager;

@SuppressWarnings("serial")
public class StateServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(StateServlet.class);
    
    private static final String CMD = "cmd";
    private static final String ID = "id";
    private static final String SESSION = "session";
    private static final String TABLE_NAME = "tableName";
    private static final String DATA = "data";
    private static final String READ_STATE = "readState";
    private static final String SAVE_STATE = "saveState";
    private static final String REMOVE_STATE = "removeState";
    private static final String SUCCESS_TRUE = "{success:true}";
    private static final String SUCCESS_FALSE = "{success:false}";
    private static final String APPLICATION_JSON = "application/json";
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         * Request parameters are cmd, id, user, session, and data
         */
        response.setContentType(APPLICATION_JSON);
        try {
            if (request.getParameter(CMD) != null){
                if (request.getParameter(CMD).equals(READ_STATE)){
                    response.getOutputStream().write(StateManager.readState(new Long(request.getParameter(ID)), request.getParameter(SESSION)).getBytes());
                }
            }
            response.getOutputStream().write(SUCCESS_TRUE.getBytes());
            response.getOutputStream().flush();
        } catch (Throwable t){
            logger.error("Exception in StateServlet", t);
            response.getOutputStream().write(SUCCESS_FALSE.getBytes());
            response.getOutputStream().flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         * Request parameters are cmd, id, user, session, and data
         */
        response.setContentType(APPLICATION_JSON);
        try {
            if (request.getParameter(CMD) != null){
                if (request.getParameter(CMD).equals(SAVE_STATE)){
                    String state = request.getParameter(DATA);
                    state = state.substring(1, state.length()-1);
                    StateManager.saveState(new Long(request.getParameter(ID)), request.getParameter(SESSION), state);
                } else if (request.getParameter(CMD).equals(REMOVE_STATE)){
                    String tname = request.getParameter(TABLE_NAME);
                    if (tname != null){
                        StateManager.removeState(new Long(request.getParameter(ID)), request.getParameter(SESSION), tname+"GridState");
                        StateManager.removeState(new Long(request.getParameter(ID)), request.getParameter(SESSION), tname+"GridDsLimit");
                        StateManager.removeState(new Long(request.getParameter(ID)), request.getParameter(SESSION), tname+"GridDsStart");
                        StateManager.removeState(new Long(request.getParameter(ID)), request.getParameter(SESSION), tname+"GridGroupBy");
                    } else {
                        StateManager.removeState(new Long(request.getParameter(ID)), request.getParameter(SESSION));
                    }
                }
            }
            response.getOutputStream().write(SUCCESS_TRUE.getBytes());
            response.getOutputStream().flush();
        } catch (Throwable t){
            logger.error("Exception in StateServlet", t);
            response.getOutputStream().write(SUCCESS_FALSE.getBytes());
            response.getOutputStream().flush();
        }        
    }
    
}
