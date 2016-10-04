package com.bc.ejb;


import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.bc.dao.BaseDao;
import com.bc.orm.CustomerShipping;

@Stateless
public class CustomerShippingSession implements CustomerShippingSessionLocal {

    public static final String LocalJNDIString = "inventory/"+CustomerShippingSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = CustomerShippingSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(CustomerShippingSession.class);
        
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public CustomerShipping findById(Long id) throws NoResultException {
        BaseDao<CustomerShipping> dao = new BaseDao<CustomerShipping>(CustomerShipping.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public CustomerShipping findById(Long id, String... joins) throws NoResultException {
        BaseDao<CustomerShipping> dao = new BaseDao<CustomerShipping>(CustomerShipping.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(CustomerShipping customerShipping) {
        BaseDao<CustomerShipping> dao = new BaseDao<CustomerShipping>(CustomerShipping.class);
        dao.create(customerShipping, customerShipping.getCustomer().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(CustomerShipping customerShipping) {
        BaseDao<CustomerShipping> dao = new BaseDao<CustomerShipping>(CustomerShipping.class);
        dao.update(customerShipping, customerShipping.getCustomer().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<CustomerShipping> dao = new BaseDao<CustomerShipping>(CustomerShipping.class);
        CustomerShipping cs = dao.findById(id);
        dao.delete(cs, cs.getCustomer().getId());
    }
    
}
