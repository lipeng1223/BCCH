package com.bc.ejb.bellwether;


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
import com.bc.orm.BellCustomer;
import com.bc.orm.BellCustomerShipping;
import com.bc.struts.QueryInput;
import com.bc.util.cache.BellCustomerCache;

@Stateless
public class BellCustomerSession implements BellCustomerSessionLocal {

    public static final String LocalJNDIString = "inventory/"+BellCustomerSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BellCustomerSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(BellCustomerSession.class);

    
    public void initCache() {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        Criteria crit = dao.getSession().createCriteria(BellCustomer.class);
        crit.setFetchMode("bellCustomerShippings", FetchMode.JOIN);
        for (BellCustomer cust : (List<BellCustomer>)crit.list()){
            BellCustomerCache.put(cust);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<BellCustomer> findByName(String name) {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        Criteria crit = dao.getSession().createCriteria(BellCustomer.class);
        crit.add(Restrictions.eq("companyName", name));
        return crit.list();
    }
    
    public Integer getCount() {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellCustomer findById(Long id) throws NoResultException {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellCustomer findById(Long id, String... joins) throws NoResultException {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellCustomerShipping findShippingById(Long id) {
        BaseDao<BellCustomerShipping> dao = new BaseDao<BellCustomerShipping>(BellCustomerShipping.class);
        return dao.findById(id);
    }    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellCustomer customer) {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        dao.create(customer, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellCustomer customer) {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        dao.update(customer, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BellCustomer> dao = new BaseDao<BellCustomer>(BellCustomer.class);
        return dao.findAll(queryInput, joins);
    }

    
}
