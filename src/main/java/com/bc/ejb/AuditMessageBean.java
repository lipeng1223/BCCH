package com.bc.ejb;


import java.sql.PreparedStatement;
import java.sql.Timestamp;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.bc.dao.BaseDao;
import com.bc.orm.Audit;
import com.bc.util.AuditMessage;

@MessageDriven(name="AuditMessageBean", activationConfig = {
    @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
    @ActivationConfigProperty(propertyName="destination", propertyValue="queue/AuditQueue")
})
public class AuditMessageBean implements MessageListener {

    public static final String LocalJNDIString = "inventory/"+AuditMessageBean.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = AuditMessageBean.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(AuditMessageBean.class);
    
    private static final String insert = "insert into audit (auditTime, username, user_id, auditAction," +
    		"columnName1, previousValue1, currentValue1, " +
            "columnName2, previousValue2, currentValue2, " +
            "columnName3, previousValue3, currentValue3, " +
            "columnName4, previousValue4, currentValue4, " +
            "columnName5, previousValue5, currentValue5, " +
            "columnName6, previousValue6, currentValue6, " +
            "columnName7, previousValue7, currentValue7, " +
            "columnName8, previousValue8, currentValue8, " +
            "columnName9, previousValue9, currentValue9, " +
            "columnName10, previousValue10, currentValue10, " +
            "columnName11, previousValue11, currentValue11, " +
            "columnName12, previousValue12, currentValue12, " +
            "columnName13, previousValue13, currentValue13, " +
            "columnName14, previousValue14, currentValue14, " +
            "columnName15, previousValue15, currentValue15, " +
            "columnName16, previousValue16, currentValue16, " +
            "columnName17, previousValue17, currentValue17, " +
            "columnName18, previousValue18, currentValue18, " +
            "columnName19, previousValue19, currentValue19, " +
            "columnName20, previousValue20, currentValue20, tableId, tableName, parentTableId, auditMessage) " +
    		"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
    		"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
    @SuppressWarnings("deprecation")
    @Override
    public void onMessage(Message msg) {
        try {
            ObjectMessage om = (ObjectMessage)msg;
            AuditMessage am = (AuditMessage)om.getObject();
            
            if (am.getAudit() != null && am.getUserId() > -1){
                BaseDao<Audit> dao = new BaseDao<Audit>(Audit.class);
                PreparedStatement ps = dao.getSession().connection().prepareStatement(insert);
                Audit audit = am.getAudit();
                ps.setTimestamp(1, new Timestamp(audit.getAuditTime().getTime()));
                ps.setString(2, audit.getUsername());
                ps.setLong(3, audit.getUserId());
                ps.setString(4, audit.getAuditAction());
                
                ps.setString(5, audit.getColumnName1());
                ps.setString(6, audit.getPreviousValue1());
                ps.setString(7, audit.getCurrentValue1());
                ps.setString(8, audit.getColumnName2());
                ps.setString(9, audit.getPreviousValue2());
                ps.setString(10, audit.getCurrentValue2());
                ps.setString(11, audit.getColumnName3());
                ps.setString(12, audit.getPreviousValue3());
                ps.setString(13, audit.getCurrentValue3());
                ps.setString(14, audit.getColumnName4());
                ps.setString(15, audit.getPreviousValue4());
                ps.setString(16, audit.getCurrentValue4());
                ps.setString(17, audit.getColumnName5());
                ps.setString(18, audit.getPreviousValue5());
                ps.setString(19, audit.getCurrentValue5());
                ps.setString(20, audit.getColumnName6());
                ps.setString(21, audit.getPreviousValue6());
                ps.setString(22, audit.getCurrentValue6());
                ps.setString(23, audit.getColumnName7());
                ps.setString(24, audit.getPreviousValue7());
                ps.setString(25, audit.getCurrentValue7());
                ps.setString(26, audit.getColumnName8());
                ps.setString(27, audit.getPreviousValue8());
                ps.setString(28, audit.getCurrentValue8());
                ps.setString(29, audit.getColumnName9());
                ps.setString(30, audit.getPreviousValue9());
                ps.setString(31, audit.getCurrentValue9());
                ps.setString(32, audit.getColumnName10());
                ps.setString(33, audit.getPreviousValue10());
                ps.setString(34, audit.getCurrentValue10());
                ps.setString(35, audit.getColumnName11());
                ps.setString(36, audit.getPreviousValue11());
                ps.setString(37, audit.getCurrentValue11());
                ps.setString(38, audit.getColumnName12());
                ps.setString(39, audit.getPreviousValue12());
                ps.setString(40, audit.getCurrentValue12());
                ps.setString(41, audit.getColumnName13());
                ps.setString(42, audit.getPreviousValue13());
                ps.setString(43, audit.getCurrentValue13());
                ps.setString(44, audit.getColumnName14());
                ps.setString(45, audit.getPreviousValue14());
                ps.setString(46, audit.getCurrentValue14());
                ps.setString(47, audit.getColumnName15());
                ps.setString(48, audit.getPreviousValue15());
                ps.setString(49, audit.getCurrentValue15());
                ps.setString(50, audit.getColumnName16());
                ps.setString(51, audit.getPreviousValue16());
                ps.setString(52, audit.getCurrentValue16());
                ps.setString(53, audit.getColumnName17());
                ps.setString(54, audit.getPreviousValue17());
                ps.setString(55, audit.getCurrentValue17());
                ps.setString(56, audit.getColumnName18());
                ps.setString(57, audit.getPreviousValue18());
                ps.setString(58, audit.getCurrentValue18());
                ps.setString(59, audit.getColumnName19());
                ps.setString(60, audit.getPreviousValue19());
                ps.setString(61, audit.getCurrentValue19());
                ps.setString(62, audit.getColumnName20());
                ps.setString(63, audit.getPreviousValue20());
                ps.setString(64, audit.getCurrentValue20());

                ps.setLong(65, audit.getTableId());
                ps.setString(66, audit.getTableName());
                ps.setLong(67, audit.getParentTableId());
                ps.setString(68, audit.getAuditMessage());
                
                ps.executeUpdate();
            }
            
        } catch (Exception e){
            logger.error("Could not audit the message sent in", e);
        }
    }
    
}
