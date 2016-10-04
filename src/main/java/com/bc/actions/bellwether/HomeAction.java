package com.bc.actions.bellwether;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.util.ActionRole;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bellwether")
@Results({
    @Result(name="bellhomepage", location="/WEB-INF/jsp/bellwether/home.jsp")
})
public class HomeAction extends BaseAction {
    
    private Map<String, Map<String, String>> searchMap = new HashMap<String, Map<String, String>>();
    private String inventorySearchDefault;
    
    @ActionRole({"BellInvAdmin", "BellInvViewer", 
                 "BellRecAdmin", "BellRecViewer", 
                 "BellOrderAdmin", "BellOrderViewer"})
	public String execute(){
        
        InventoryAction ia = new InventoryAction();
        ia.searchWin(); //setup listTable for filter magic
        searchMap.put("inventory", ia.getSearchNames());
        inventorySearchDefault = "ISBN";

        OrderAction oa = new OrderAction();
        oa.searchWin(); //setup listTable for filter magic
        searchMap.put("order", oa.getSearchNames());
        
        ReceivingAction ra = new ReceivingAction();
        ra.searchWin(); //setup listTable for filter magic
        searchMap.put("receiving", ra.getSearchNames());
        
		return "bellhomepage";
	}
	
    public Map<String, Map<String, String>> getSearchMap() {
        return searchMap;
    }

    public void setSearchMap(Map<String, Map<String, String>> searchMap) {
        this.searchMap = searchMap;
    }

    public String getInventorySearchDefault() {
        return inventorySearchDefault;
    }

    public void setInventorySearchDefault(String inventorySearchDefault) {
        this.inventorySearchDefault = inventorySearchDefault;
    }
    
}
