package com.bc.servlet;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.bc.ejb.CustomerSession;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.VendorSession;
import com.bc.ejb.VendorSessionLocal;
import com.bc.ejb.bellwether.BellCustomerSession;
import com.bc.ejb.bellwether.BellCustomerSessionLocal;
import com.bc.ejb.bellwether.BellVendorSession;
import com.bc.ejb.bellwether.BellVendorSessionLocal;
import com.bc.socketserver.SocketServerPipelineFactory;
import com.bc.util.cache.StateCache;

@SuppressWarnings("serial")
public class StartupServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(StartupServlet.class);
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        logger.info("Startup Servlet init...");
        try {
            // Configure the server.
            ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

            // Set up the pipeline factory.
            bootstrap.setPipelineFactory(new SocketServerPipelineFactory());

            // Bind and start to accept incoming connections.
            
            bootstrap.bind(new InetSocketAddress(8888));
            
            logger.info("Netty socket server is up and running on 8888.");
        } catch (Throwable t){
            logger.error("Failed to startup", t);
        }
        
        
        try {
            logger.info("Loading vendors into cache...");
            getVendorSession().initCache();
        } catch (Exception e){
            logger.fatal("Could not load vendors into cache", e);
        }
        
        try {
            logger.info("Loading bell vendors into cache...");
            getBellVendorSession().initCache();
        } catch (Exception e){
            logger.fatal("Could not load bell vendors into cache", e);
        }
        
        try {
            logger.info("Loading customers into cache...");
            getCustomerSession().initCache();
        } catch (Exception e){
            logger.fatal("Could not load customers into cache", e);
        }
        
        try {
            logger.info("Loading bell customers into cache...");
            getBellCustomerSession().initCache();
        } catch (Exception e){
            logger.fatal("Could not load bell customers into cache", e);
        }
        
        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shuting down the EHCache...");
                StateCache.shutdown();
                logger.info("EHCache has been shutdown.");
            }
        });
        
        logger.info("Finished Startup Servlet.");
    }
    
    
    public VendorSessionLocal getVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIStringNoLoader);
            }
            return (VendorSessionLocal)ctx.lookup(VendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup VendorSession", ne);
        }
        throw new RuntimeException("Could not lookup VendorSession");
    }


    public CustomerSessionLocal getCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIStringNoLoader);
            }
            return (CustomerSessionLocal)ctx.lookup(CustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }

    public BellVendorSessionLocal getBellVendorSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellVendorSessionLocal)ctx.lookup(BellVendorSession.LocalJNDIStringNoLoader);
            }
            return (BellVendorSessionLocal)ctx.lookup(BellVendorSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup VendorSession", ne);
        }
        throw new RuntimeException("Could not lookup BellVendorSession");
    }

        public BellCustomerSessionLocal getBellCustomerSession() {
        try {
            Context ctx =   new InitialContext();
            if (System.getProperty("testing") != null){
                return (BellCustomerSessionLocal)ctx.lookup(BellCustomerSession.LocalJNDIStringNoLoader);
            }
            return (BellCustomerSessionLocal)ctx.lookup(BellCustomerSession.LocalJNDIString);
        } catch (NamingException ne){
            logger.fatal("Could not lookup CustomerSession", ne);
        }
        throw new RuntimeException("Could not lookup CustomerSession");
    }

}
