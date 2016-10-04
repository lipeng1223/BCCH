package com.bc.ejb;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.Customer;
import com.bc.struts.QueryInput;
import com.bc.util.cache.CustomerCache;

@Stateless
public class CustomerSession implements CustomerSessionLocal {

    public static final String LocalJNDIString = "inventory/"+CustomerSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = CustomerSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(CustomerSession.class);

    
    public void initCache() {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        Criteria crit = dao.getSession().createCriteria(Customer.class);
        crit.setFetchMode("customerShippings", FetchMode.JOIN);
        for (Customer cust : (List<Customer>)crit.list()){
            CustomerCache.put(cust);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Customer> findByName(String name) {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        Criteria crit = dao.getSession().createCriteria(Customer.class);
        crit.add(Restrictions.eq("companyName", name));
        return crit.list();
    }
    
    public Integer getCount() {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Customer findById(Long id) throws NoResultException {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Customer findById(Long id, String... joins) throws NoResultException {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Customer customer) {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        dao.create(customer, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Customer customer) {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        dao.update(customer, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        Customer cust = dao.findById(id);
        if (cust != null){
            cust.setDeleted(true);
            dao.update(cust, null);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<Customer> dao = new BaseDao<Customer>(Customer.class);
        return dao.findAll(queryInput, joins);
    }

    
}
