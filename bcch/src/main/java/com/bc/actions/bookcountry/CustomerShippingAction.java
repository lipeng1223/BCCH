package com.bc.actions.bookcountry;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.actions.BaseAction;
import com.bc.ejb.CustomerSessionLocal;
import com.bc.ejb.CustomerShippingSessionLocal;
import com.bc.orm.CustomerShipping;
import com.bc.orm.Customer;
import com.bc.util.ActionRole;
import com.bc.util.cache.CustomerCache;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="crud", location="/WEB-INF/jsp/bookcountry/customers/shipping/crud.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class CustomerShippingAction extends BaseAction {

    private CustomerShipping customerShipping;
    private Long customerId;
    
    @ActionRole({"BcCustomerAdmin"})
    public String create(){
        return "crud";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String createSubmit(){
        try {
            CustomerShippingSessionLocal csSession = getCustomerShippingSession();
            CustomerSessionLocal customerSession = getCustomerSession();
            customerShipping.setCustomer(customerSession.findById(customerId));
            Customer cust = customerSession.findById(customerId, "customerShippings");
            if (cust.getCustomerShippings().size() == 0){
                customerShipping.setDefaultShip(true);
            }
            csSession.create(customerShipping);
            id = customerShipping.getId();
            CustomerCache.remove(customerId);
            CustomerCache.put(customerSession.findById(customerId, "customerShippings"));
            setSuccess(true);
            setMessage("Created New Customer Shipping Address");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not create the Customer Shipping Address, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String edit(){
        try {
            CustomerShippingSessionLocal csSession = getCustomerShippingSession();
            customerShipping = csSession.findById(id);
        } catch (Exception e){
            logger.error("Could not find customerShipping by id: "+id, e);
            return "error";
        }
        return "crud";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String editSubmit(){
        try {
            CustomerShippingSessionLocal csSession = getCustomerShippingSession();
            CustomerShipping dbcs = csSession.findById(customerShipping.getId(), "customer");
            if (dbcs != null){
                dbcs.setAddress1(customerShipping.getAddress1());
                dbcs.setAddress2(customerShipping.getAddress2());
                dbcs.setAddress3(customerShipping.getAddress3());
                dbcs.setCity(customerShipping.getCity());
                dbcs.setCode(customerShipping.getCode());
                dbcs.setComment(customerShipping.getComment());
                dbcs.setCountry(customerShipping.getCountry());
                dbcs.setEmail(customerShipping.getEmail());
                dbcs.setFax(customerShipping.getFax());
                dbcs.setHomePhone(customerShipping.getHomePhone());
                dbcs.setPhone(customerShipping.getPhone());
                dbcs.setShippingCompany(customerShipping.getShippingCompany());
                dbcs.setShippingName(customerShipping.getShippingName());
                dbcs.setState(customerShipping.getState());
                dbcs.setWorkPhone(customerShipping.getWorkPhone());
                dbcs.setWorkExt(customerShipping.getWorkExt());
                dbcs.setZip(customerShipping.getZip());
                csSession.update(dbcs);
            }
            CustomerCache.remove(dbcs.getCustomer().getId());
            CustomerSessionLocal customerSession = getCustomerSession();
            CustomerCache.put(customerSession.findById(dbcs.getCustomer().getId(), "customerShippings"));
            setSuccess(true);
            setMessage("Updated Customer Shipping Address");
        } catch (Exception e){
            logger.error("Exception", e);
            setSuccess(false);
            setMessage("Could not update the Customer Shipping Address, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcCustomerAdmin"})
    public String delete(){
        try {
            CustomerShippingSessionLocal csSession = getCustomerShippingSession();
            CustomerShipping dbcs = csSession.findById(id, "customer");
            dbcs.setDeleted(true);
            csSession.update(dbcs);
            CustomerCache.remove(dbcs.getCustomer().getId());
            CustomerSessionLocal customerSession = getCustomerSession();
            CustomerCache.put(customerSession.findById(dbcs.getCustomer().getId(), "customerShippings"));
            setSuccess(true);
            setMessage("Deleted the Customer Shipping Address.");
        } catch (Exception e){
            logger.error("Could not delete customerShipping", e);
            setSuccess(false);
            setMessage("Could not delete the Customer Shipping Address, there was a system error");
        }
        return "status";
    }

    @ActionRole({"BcCustomerAdmin"})
    public String defaultShip(){
        try {
            CustomerShippingSessionLocal csSession = getCustomerShippingSession();
            CustomerShipping dbcs = csSession.findById(id, "customer");
            dbcs.setDefaultShip(true);
            csSession.update(dbcs);
            CustomerCache.remove(dbcs.getCustomer().getId());
            CustomerSessionLocal customerSession = getCustomerSession();
            Customer cust = customerSession.findById(dbcs.getCustomer().getId(), "customerShippings");
            for (CustomerShipping cs : cust.getCustomerShippings()){
                if (cs.getDefaultShip() && !cs.getId().equals(id)){
                    cs.setDefaultShip(false);
                    csSession.update(cs);
                }
            }
            CustomerCache.put(customerSession.findById(dbcs.getCustomer().getId(), "customerShippings"));
            setSuccess(true);
            setMessage("Deleted the Customer Shipping Address.");
        } catch (Exception e){
            logger.error("Could not delete customerShipping", e);
            setSuccess(false);
            setMessage("Could not delete the Customer Shipping Address, there was a system error");
        }
        return "status";
    }

    public CustomerShipping getCustomerShipping() {
        return customerShipping;
    }

    public void setCustomerShipping(CustomerShipping customerShipping) {
        this.customerShipping = customerShipping;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    
}
