package com.bc.actions.bookcountry;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="bchomepage", location="/WEB-INF/jsp/bookcountry/home.jsp")
})
public class HomeAction extends BaseAction {
    
    private Map<String, Map<String, String>> searchMap = new HashMap<String, Map<String, String>>();
    
    private String inventorySearchDefault;

    
    @ActionRole({"BcInvAdmin", "BcInvViewer", "BcRecAdmin", "BcRecViewer", 
                 "BcOrderAdmin", "BcOrderViewer", "BcCustomerAdmin", "BcCustomerViewer",
                 "BcVendorAdmin", "BcVendorViewer", "BcUserAdmin", "BcUserViewer"})                 
	public String execute() {

        InventoryItemAction ia = new InventoryItemAction();
        ia.searchWin(); //setup listTable for filter magic
        searchMap.put("inventory", ia.getSearchNames());
        inventorySearchDefault = "ISBN";

        OrderAction oa = new OrderAction();
        oa.searchWin(); //setup listTable for filter magic
        searchMap.put("order", oa.getSearchNames());
        
        ReceivingAction ra = new ReceivingAction();
        ra.searchWin(); //setup listTable for filter magic
        searchMap.put("receiving", ra.getSearchNames());

        ManifestAction ma = new ManifestAction();
        ma.searchWin(); //setup listTable for filter magic
        searchMap.put("manifest", ma.getSearchNames());
        
		return "bchomepage";
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
