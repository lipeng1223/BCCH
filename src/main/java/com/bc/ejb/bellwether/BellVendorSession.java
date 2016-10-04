package com.bc.ejb.bellwether;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.BellVendor;
import com.bc.struts.QueryInput;
import com.bc.util.cache.BellVendorCache;

@Stateless
public class BellVendorSession implements BellVendorSessionLocal {

    public static final String LocalJNDIString = "inventory/"+BellVendorSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BellVendorSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(BellVendorSession.class);

    public void initCache() {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        Criteria crit = dao.getSession().createCriteria(BellVendor.class);
        for (BellVendor vendor : (List<BellVendor>)crit.list()){
            BellVendorCache.put(vendor);
        }
    }
    
    public Integer getCount() {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellVendor findById(Long id) throws NoResultException {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BellVendor findById(Long id, String... joins) throws NoResultException {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellVendor vendor) {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        dao.create(vendor, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellVendor vendor) {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        dao.update(vendor, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BellVendor> dao = new BaseDao<BellVendor>(BellVendor.class);
        return dao.findAll(queryInput, joins);
    }

}
