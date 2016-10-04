package com.bc.actions;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.DaoResults;
import com.bc.ejb.AuditSessionLocal;
import com.bc.struts.QueryInput;
import com.bc.util.ActionRole;
import com.bc.orm.Audit;
import org.hibernate.criterion.MatchMode;

@ParentPackage("bcpackage")
@Namespace("/secure")
@Results({
    @Result(name="history", location="/WEB-INF/jsp/history.jsp")
})
public class EntityHistoryAction extends BaseAction {
	
    private static final Logger logger = Logger.getLogger(EntityHistoryAction.class);

    private String tableName;
    private String childTableName;
    private String isbn;
    private Long tableId;
    private DaoResults daoResults;
    private DaoResults childrenResults;
    private String auditTitle;
    private String childAuditTitle;
    
    @ActionRole({"WebUser"})
    public String execute(){
        try {
            if (tableName != null){
                AuditSessionLocal auditSession = getAuditSession();

                
                if ((tableId == null || tableId < 0) && isbn != null && isbn.length() > 0) {
                    // attempt to get the tableId from the audit log
                    queryInput = new QueryInput();
                    queryInput.addAndCriterion(Restrictions.eq("tableName", tableName));
                    queryInput.addAndCriterion(Restrictions.like("auditMessage", isbn, MatchMode.ANYWHERE));
                    queryInput.addAndCriterion(Restrictions.eq("auditAction", "create"));
                    queryInput.setLimit(1);
                    queryInput.setSortCol("auditTime");
                    queryInput.setSortDir(QueryInput.SORT_DESC);
                    daoResults = auditSession.findAll(queryInput);
                    if (daoResults != null && daoResults.getData() != null && daoResults.getData().size() > 0){
                        Audit a = (Audit)daoResults.getData().get(0);
                        tableId = a.getTableId();
                    }
                }
                
                queryInput = new QueryInput();
                queryInput.addAndCriterion(Restrictions.eq("tableName", tableName));
                if (tableId != null && tableId > -1){
                    queryInput.addAndCriterion(Restrictions.eq("tableId", tableId));
                } else if (isbn != null && isbn.length() > 0){
                    queryInput.addAndCriterion(Restrictions.like("auditMessage", isbn, MatchMode.ANYWHERE));
                } else {
                    tableId = null;
                    isbn = null;
                    return "history";
                }
                queryInput.setLimit(200);
                queryInput.setSortCol("auditTime");
                queryInput.setSortDir(QueryInput.SORT_DESC);
                daoResults = auditSession.findAll(queryInput);
                
                // look for children audits if childTableName is defined
                if (childTableName != null && childTableName.length() > 0){
                    queryInput = new QueryInput();
                    queryInput.addAndCriterion(Restrictions.eq("tableName", childTableName));
                    queryInput.addAndCriterion(Restrictions.eq("parentTableId", tableId));
                    queryInput.setLimit(200);
                    queryInput.setSortCol("auditTime");
                    queryInput.setSortDir(QueryInput.SORT_DESC);
                    childrenResults = auditSession.findAll(queryInput);
                }
            }
        } catch (Exception e){
            logger.error("Could not get history for table name: "+tableName+" id: "+tableId, e);
        }
        return "history";
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public DaoResults getDaoResults() {
        return daoResults;
    }

    public void setDaoResults(DaoResults daoResults) {
        this.daoResults = daoResults;
    }

    public String getChildTableName() {
        return childTableName;
    }

    public void setChildTableName(String childTableName) {
        this.childTableName = childTableName;
    }

    public DaoResults getChildrenResults() {
        return childrenResults;
    }

    public void setChildrenResults(DaoResults childrenResults) {
        this.childrenResults = childrenResults;
    }

    public String getAuditTitle() {
        return auditTitle;
    }

    public void setAuditTitle(String auditTitle) {
        this.auditTitle = auditTitle;
    }

    public String getChildAuditTitle() {
        return childAuditTitle;
    }

    public void setChildAuditTitle(String childAuditTitle) {
        this.childAuditTitle = childAuditTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
}
