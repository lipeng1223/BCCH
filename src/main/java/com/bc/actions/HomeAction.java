package com.bc.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.bookcountry.InventoryItemAction;
import com.bc.actions.bookcountry.ManifestAction;
import com.bc.actions.bookcountry.OrderAction;
import com.bc.actions.bookcountry.ReceivingAction;
import com.bc.table.ColumnModel;
import com.bc.table.Filter;
import com.bc.table.Table;
import com.bc.util.ActionRole;

@ParentPackage("bcpackage")
@Namespace("/secure")
@Results({
    @Result(name="homepage", location="/WEB-INF/jsp/home.jsp"),
    @Result(name="bchome", location="/secure/bookcountry/home.bc", type="redirect"),
    @Result(name="bellhome", location="/secure/bellwether/home.bc", type="redirect")
})
public class HomeAction extends BaseAction {
	
    private Map<String, Map<String, String>> searchMap = new HashMap<String, Map<String, String>>(); 
        
    @ActionRole({"WebUser"})
	public String execute(){
	    // gather system information
        
        InventoryItemAction ia = new InventoryItemAction();
        ia.searchWin(); //setup listTable for filter magic
        searchMap.put("inventory", ia.getSearchNames());
        
        OrderAction oa = new OrderAction();
        oa.searchWin(); //setup listTable for filter magic
        searchMap.put("order", oa.getSearchNames());
        
        ReceivingAction ra = new ReceivingAction();
        ra.searchWin(); //setup listTable for filter magic
        searchMap.put("receiving", ra.getSearchNames());
 
        ManifestAction ma = new ManifestAction();
        ma.searchWin(); //setup listTable for filter magic
        searchMap.put("manifest", ma.getSearchNames());
        
		return "homepage";
	}

	public String bookcountryHome(){
		return "bchome";
	}
	
	public String bellwetherHome(){
		return "bellhome";
	}

    public Map<String, Map<String, String>> getSearchMap() {
        return searchMap;
    }

    public void setSearchMap(Map<String, Map<String, String>> searchMap) {
        this.searchMap = searchMap;
    }
    
}
