package com.bc.actions;

import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.util.ActionRole;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure")
@Results({
    @Result(name="quick", location="${quickSearchUrl}", type="redirect")
})
public class QuickSearchAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(QuickSearchAction.class);
    
    private String quickSearch;
    private String quickSearchUrl;
    private String quickSearchLocation;
    
    @ActionRole({"BcInvAdmin", "BcInvViewer", "BcOrderAdmin", "BcOrderViewer", "BcRecAdmin", "BcRecViewer", "BcManifestAdmin", "BcManifestViewer", "BcCustomerAdmin", "BcCustomerViewer", "BcVendorAdmin", "BcVendorViewer"})
    public String quickSearch(){
        try {
            if (quickSearchLocation.equals("inventory")){
                quickSearchUrl = "/secure/bookcountry/inventoryitem!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            } else if (quickSearchLocation.equals("orders")){
                quickSearchUrl = "/secure/bookcountry/order!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            } else if (quickSearchLocation.equals("receiving")){
                quickSearchUrl = "/secure/bookcountry/receiving!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            } else if (quickSearchLocation.equals("manifests")){
                quickSearchUrl = "/secure/bookcountry/manifest!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            } else if (quickSearchLocation.equals("customers")){
                quickSearchUrl = "/secure/bookcountry/customer!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            } else if (quickSearchLocation.equals("vendors")){
                quickSearchUrl = "/secure/bookcountry/vendor!quickSearch.bc?quickSearchLocation="+quickSearchLocation+"&quickSearch="+URLEncoder.encode(quickSearch, "UTF-8");
            }
        } catch (Exception e){
            logger.error("Could not execute quick search", e);
        }
        return "quick";
    }
    
    public String getQuickSearchUrl(){
        return quickSearchUrl;
    }

    public String getQuickSearch() {
        return quickSearch;
    }

    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }

    public String getQuickSearchLocation() {
        return quickSearchLocation;
    }

    public void setQuickSearchLocation(String quickSearchLocation) {
        this.quickSearchLocation = quickSearchLocation;
    }
    
}
