package com.bc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.bc.orm.Audit;
import com.bc.orm.Auditable;
import com.bc.orm.BaseEntity;
import org.hibernate.LazyInitializationException;


public class PropertyAuditLogger {

    private static final Logger logger = Logger.getLogger(PropertyAuditLogger.class);
    
    private static HashSet<String> ignoredMethods;
    
    static {
        ignoredMethods = new HashSet<String>();
        ignoredMethods.add("getLastUpdate");
        ignoredMethods.add("getLastUpdateBy");
    }

    private PropertyAuditLogger(){}
    
    public static void logDiff(BaseEntity orig, BaseEntity updated, Long parentOid){
        if (orig instanceof Auditable) {
            Method[] methods = orig.getClass().getMethods();
            try {
                int colPos = 1;
                boolean submitTheAudit = false;
                Audit audit = null;
                String tableName = "";
                for (Annotation a : orig.getClass().getAnnotations()){
                    if (a instanceof javax.persistence.Table){
                        tableName = ((javax.persistence.Table)a).name();
                    }
                }
                //logger.info("tableId: "+orig.getId()+" tableName: "+tableName);
                for(Method method : methods){
                    if(isWatchedProperty(method)) {
                        Object origVal = method.invoke(orig);
                        Object updateVal = method.invoke(updated);
                        if (origVal != null && updateVal != null){
                            if (!origVal.equals(updateVal)){
                                if (updateVal instanceof BaseEntity && !org.hibernate.Hibernate.isInitialized(updateVal)){
                                    // not doing anything on uninitialized base objects
                                    continue;
                                }
                                //logger.info("difference for "+method.getName().substring(3).toLowerCase()+": "+origVal+" : "+updateVal);
                                if (audit == null) audit = new Audit(orig.getId(), tableName);
                                audit.setColumnName(colPos, method.getName().substring(3).toLowerCase());
                                audit.setCurrentValue(colPos,getStringVal(updateVal));
                                audit.setPreviousValue(colPos, getStringVal(origVal));
                                colPos++;
                                submitTheAudit = true;
                            }
                        }
                    }
                }
                if (submitTheAudit){
                    //String username = getUsername();
                    Calendar now = Calendar.getInstance();
                    String username = ThreadContext.get("username");
                    Long userId = new Long(ThreadContext.get("userId"));
                    audit.setAuditTime(now.getTime());
                    //logger.info("parentOid: "+parentOid);
                    audit.setParentTableId(parentOid);
                    audit.setAuditMessage(orig.getAuditMessage());
                    audit.setAuditAction(ThreadContext.get("userAction"));
                    audit.setUserId(userId);
                    audit.setUsername(username);
                    
                    // queue up the difference to get pushed into the database
                    AuditMessage am = new AuditMessage();
                    am.setAudit(audit);
                    am.setUserId(userId);
                    am.setUsername(username);
                    am.setTime(now.getTime());
                    sendMessage(am);
                }
            } catch (Exception e){
                logger.error("Could not get diff", e);
            }
        }
    }    
    
    public static void logAction(BaseEntity entity, String action, Long parentOid){
        String tableName = "";
        for (Annotation a : entity.getClass().getAnnotations()){
            if (a instanceof javax.persistence.Table){
                tableName = ((javax.persistence.Table)a).name();
            }
        }
        Audit audit = new Audit(entity.getId(), tableName);
        Calendar now = Calendar.getInstance();
        String username = ThreadContext.get("username");
        Long userId = new Long(ThreadContext.get("userId"));
        audit.setAuditMessage(entity.getAuditMessage());
        audit.setAuditTime(now.getTime());
        audit.setParentTableId(parentOid);
        audit.setAuditAction(action);
        audit.setUserId(userId);
        audit.setUsername(username);
        
        // queue up the difference to get pushed into the database
        AuditMessage am = new AuditMessage();
        am.setAudit(audit);
        am.setUserId(userId);
        am.setUsername(username);
        am.setTime(now.getTime());
        sendMessage(am);
    }
    
    private static String getStringVal(Object o){
        if (o == null){
            return null;
        }
        if (o instanceof Boolean) {
            return ""+((Boolean)o).booleanValue();
        } else if (o instanceof Character) {
            return ""+((Character)o).charValue();
        } else if (o instanceof Byte) {
            return ""+((Byte)o).byteValue();
        } else if (o instanceof Short) {
            return ""+((Short)o).shortValue();
        } else if (o instanceof Integer) {
            return ""+((Integer)o).intValue();
        } else if (o instanceof Long) {
            return ""+((Long)o).longValue();
        } else if (o instanceof Float) {
            return ""+((Float)o).floatValue();
        } else if (o instanceof Double) {
            return ""+((Double)o).doubleValue();
        } else if (o instanceof BaseEntity){
            try {
                return ((BaseEntity)o).getAuditMessage();
            } catch (LazyInitializationException lie){
                return null;
            }
        } else {
            return o.toString();
        }
    }
    
    private static void sendMessage(AuditMessage auditMessage){
        Connection connection = null;
        try {
            //get reference to JMS destination  
            Context ctx =   new InitialContext();
            Queue destination = (Queue)ctx.lookup("queue/AuditQueue");  
              
            //get reference to the ConnectionFactory  
            ConnectionFactory connectionFactory = (ConnectionFactory)ctx.lookup("QueueConnectionFactory");  
              
            connection = connectionFactory.createConnection();  
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
              
            Message msg = session.createObjectMessage(auditMessage);
              
            MessageProducer producer = session.createProducer(destination);  
            producer.send(msg);  
            //logger.info("Added auditMessage to the queue.");         
        } catch (JMSException jmse){
            logger.error("Could not add AuditMessage to the AuditQueue", jmse);
        } catch (NamingException ne){
            logger.error("Could not lookup the queue/AuditQueue or the QueueConnectionFactory", ne);
        } finally {
            if (connection != null){
                try {
                    connection.close();
                } catch (Exception ex){}
            }
        }
    }
    
    private static boolean isWatchedProperty(Method method){
        if(!method.getName().startsWith("get"))      return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        if (method.getReturnType().equals(Collection.class)) return false;
        if (method.getReturnType().equals(Set.class)) return false;
        if (ignoredMethods.contains(method.getName())) return false;
        Annotation[] annotations = method.getAnnotations();
        if (annotations != null){
            for (Annotation a : annotations){
                if (a.annotationType().getCanonicalName().equals("javax.persistence.Transient")) return false; // not logging transients
                //if (a.annotationType().getCanonicalName().equals("javax.persistence.ManyToOne")) return false; // not logging manytoone
            }
        }
        return true;
    }    
    
}
