package com.bc.actions.bookcountry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.ejb.UserSessionLocal;
import com.bc.orm.User;
import com.bc.orm.UserRole;
import com.bc.struts.QueryResults;
import com.bc.table.ColumnData;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.table.Toolbar;
import com.bc.table.ToolbarButton;
import com.bc.util.ActionRole;
import com.bc.util.MD5Hash;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="list", location="/WEB-INF/jsp/users/list.jsp"),
    @Result(name="crud", location="/WEB-INF/jsp/users/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp"),
    @Result(name="queryresults", location="/WEB-INF/jsp/queryresults.jsp")
})
public class UserAction extends BaseAction {

    private User user;
    private Table listTable;
    private String[] userRoles;
    
    @ActionRole({"BcUserAdmin", "BcUserViewer"})
    public String list(){
        setupListTable();
        return "list";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String createSubmit(){
        try {
            UserSessionLocal userSession = getUserSession();
            User test = userSession.findByName(user.getUsername());
            if (test != null){
                // user already exists
                setSuccess(false);
                setMessage("This username already exists in the system.");
                return "status";
            }
            test = userSession.findByPin(user.getPin());
            if (test != null){
                setSuccess(false);
                setMessage("This pin is used by another user, please choose another.");
                return "status";
            }

            user.setPassword(MD5Hash.encode(user.getPassword()));
            userSession.create(user);
            id = user.getId();
            
            Set<UserRole> newRoles = new HashSet<UserRole>();
            for (String role : userRoles){
                UserRole ur = new UserRole();
                ur.setUser(user);
                ur.setUsername(user.getUsername());
                ur.setRole(role);
                newRoles.add(ur);
            }
            UserRole ur = new UserRole();
            ur.setUser(user);
            ur.setUsername(user.getUsername());
            ur.setRole("WebUser");
            newRoles.add(ur);
            user.setRoles(newRoles);
            userSession.update(user);
            
            setSuccess(true);
            setMessage("Created the user.");
        } catch (Exception e){
            logger.error("Could not create the user", e);
            setSuccess(false);
            setMessage("Could not create the user, system error");
        }
        return "status";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String edit(){
        UserSessionLocal userSession = getUserSession();
        user = userSession.findById(id, "roles");
        return "crud";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String editSubmit(){
        try {
            UserSessionLocal userSession = getUserSession();
            User dbUser = userSession.findById(user.getId(), "roles");
            
            User test = userSession.findByName(user.getUsername());
            if (test != null && !test.getId().equals(dbUser.getId())){
                // user already exists
                setSuccess(false);
                setMessage("This username already exists in the system.");
                return "status";
            }
            test = userSession.findByPin(user.getPin());
            if (test != null && !test.getId().equals(dbUser.getId())){
                setSuccess(false);
                setMessage("This pin is used by another user, please choose another.");
                return "status";
            }
            
            dbUser.setUsername(user.getUsername());
            dbUser.setFirstName(user.getFirstName());
            dbUser.setLastName(user.getLastName());
            if (!dbUser.getPassword().equals(user.getPassword()) && dbUser.getPassword().equals(MD5Hash.encode(user.getPassword()))) {
                dbUser.setPassword(MD5Hash.encode(user.getPassword()));
            }
            dbUser.setPin(user.getPin());
            dbUser.setEmail(user.getEmail());
            dbUser.setEmployeeId(user.getEmployeeId());
            if (!dbUser.getPassword().equals(user.getPassword())){
                dbUser.setPassword(MD5Hash.encode(user.getPassword()));
            }
            
            for (UserRole ur : dbUser.getRoles()){
                userSession.deleteRole(ur);
            }
            dbUser.setRoles(new HashSet<UserRole>());
            userSession.update(dbUser);
            dbUser = userSession.findById(user.getId());
            
            // blow out the user roles and create new ones
            Set<UserRole> newRoles = new HashSet<UserRole>();
            for (String role : userRoles){
                UserRole ur = new UserRole();
                ur.setUser(dbUser);
                ur.setUsername(dbUser.getUsername());
                ur.setRole(role);
                newRoles.add(ur);
            }
            UserRole ur = new UserRole();
            ur.setUser(dbUser);
            ur.setUsername(dbUser.getUsername());
            ur.setRole("WebUser");
            newRoles.add(ur);
            dbUser.setRoles(newRoles);
            
            userSession.update(dbUser);
            setSuccess(true);
            setMessage("Updated the user.");
        } catch (Exception e){
            logger.error("Could not update the user", e);
            setSuccess(false);
            setMessage("Could not update the user, system error");
        }
        return "status";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String delete(){
        try {
            UserSessionLocal userSession = getUserSession();
            userSession.delete(id);
            setSuccess(true);
            setMessage("Deleted the user.");
        } catch (Exception e){
            logger.error("Could not delete user", e);
            setSuccess(false);
            setMessage("Could not delete the user, there was a system error");
        }
        return "status";
    }
    
    @ActionRole({"BcUserAdmin"})
    public String enableDisable(){
        try {
            UserSessionLocal userSession = getUserSession();
            user = userSession.findById(id);
            if (user.getUsername().equals(getUserName())){
                setSuccess(false);
                setMessage("You cannot disable yourself.");
                return "status";
            }
            user.setActive(!user.getActive());
            userSession.update(user);
            setSuccess(true);
            if (user.getActive()){
                setMessage("User "+user.getUsername()+" is now enabled.");
            } else {
                setMessage("User "+user.getUsername()+" is now disabled.");
            }
        } catch (Exception e){
            logger.error("Could not delete user", e);
            setSuccess(false);
            setMessage("Could not enable / disable the user, there was a system error");
        }
        return "status";
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

    public String[] getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String[] userRoles) {
        this.userRoles = userRoles;
    }
    
}
