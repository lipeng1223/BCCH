package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.ejb.UserSessionLocal;
import com.bc.orm.User;
import com.bc.struts.QueryInput;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="audit", location="/WEB-INF/jsp/users/audit.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class UserAuditAction extends BaseAction {

    private User user;
    private Table listTable;
    
    @ActionRole({"BcUserAdmin", "BcUserViewer"})
    public String list(){
        UserSessionLocal userSession = getUserSession();
        queryInput.setSortCol("username");
        queryInput.setSortDir(QueryInput.SORT_ASC);
        queryResults = new QueryResults(userSession.findAll(queryInput));
        setupListTable();
        return "audit";
    }
    
    @ActionRole({"BcUserAdmin", "BcUserViewer"})
    public String listData(){
        setupListTable(); 
        try {
            UserSessionLocal userSession = getUserSession();
            queryResults = new QueryResults(userSession.findAll(queryInput, "roles"));
            queryResults.setTableConfig(listTable, queryInput.getFilterParams());
        } catch (Exception e){
            logger.error("Could not get list data", e);
        }
        return "queryresults";
    }
    
    private void setupListTable(){
        listTable = new Table();
        listTable.setExportable(true);
        listTable.setPageable(true);
        
        List<ColumnData> cd = new ArrayList<ColumnData>();
        cd.add(new ColumnData("id").setType("int"));
        cd.add(new ColumnData("active"));
        cd.add(new ColumnData("username"));
        cd.add(new ColumnData("pin"));
        cd.add(new ColumnData("email"));
        cd.add(new ColumnData("firstName"));
        cd.add(new ColumnData("lastName"));
        cd.add(new ColumnData("employeeId"));
        cd.add(new ColumnData("rolesDisplay"));
        listTable.setColumnDatas(cd);
        
        List<ColumnModel> cm = new ArrayList<ColumnModel>();
        cm.add(new ColumnModel("id", "ID", 50, true));
        cm.add(new ColumnModel("active", "Active", 60).setRenderer("booleanRenderer"));
        cm.add(new ColumnModel("username", "Username", 100));
        cm.add(new ColumnModel("pin", "PIN", 80));
        cm.add(new ColumnModel("email", "Email", 150));
        cm.add(new ColumnModel("firstName", "First Name", 125));
        cm.add(new ColumnModel("lastName", "Last Name", 125));
        cm.add(new ColumnModel("employeeId", "Employee ID", 100));
        cm.add(new ColumnModel("rolesDisplay", "Roles", 1200));
        listTable.setColumnModels(cm);
        
        
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter("long", "id"));
        filters.add(new Filter("string", "username"));
        filters.add(new Filter("boolean", "active"));
        filters.add(new Filter("string", "pin"));
        filters.add(new Filter("string", "email"));
        filters.add(new Filter("string", "firstName"));
        filters.add(new Filter("string", "lastName"));
        filters.add(new Filter("string", "employeeId"));
        listTable.setFilters(filters);
        
        Toolbar t = new Toolbar();
        List<ToolbarButton> buttons = new ArrayList<ToolbarButton>();
        if (getIsBcUserAdmin()){
            buttons.add(new ToolbarButton("Create", "createButtonClick", "create_icon", "Create A New User"));
            buttons.add(new ToolbarButton("Edit", "editButtonClick", "edit_icon", "Edit The Selected User").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Delete", "deleteButtonClick", "delete_icon", "Delete The Selected User").setSingleRowAction(true));
            buttons.add(new ToolbarButton("Enable/Disable", "enableDisableButtonClick", "edit_icon", "Enable / Disable The Selected User").setSingleRowAction(true));
        }
        
        t.setButtons(buttons);
        listTable.setToolbar(t);
        
        listTable.setDefaultSortCol("username");
        listTable.setDefaultSortDir(Table.SORT_DIR_ASC);
        
        // this is for the exports
        setExcelExportFileName("UserExport");
        setExcelExportSheetName("Users");
        
    }

    public Table getListTable() {
        return listTable;
    }

    public void setListTable(Table listTable) {
        this.listTable = listTable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
