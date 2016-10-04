package com.bc.ejb.bellwether;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.BellInventory;
import com.bc.orm.BellSku;
import com.bc.struts.QueryInput;

@Stateless
public class BellInventorySession implements BellInventorySessionLocal {

    public static final String LocalJNDIString = "inventory/"+BellInventorySession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = BellInventorySession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(BellInventorySession.class);
        
    public Integer getCount() {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        return dao.getCount();
    }
    
    public BellInventory findById(Long id) throws NoResultException {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        return dao.findById(id);
    }

    public BellInventory findById(Long id, String... joins) throws NoResultException {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(BellInventory bellInventory) {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        dao.create(bellInventory, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(BellInventory bellInventory) {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        dao.update(bellInventory, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<BellInventory> dao = new BaseDao<BellInventory>(BellInventory.class);
        dao.delete(dao.findById(id), null);
    }
    
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<BellInventory> iiDao = new BaseDao<BellInventory>(BellInventory.class);
        return iiDao.findAll(queryInput, joins);
    }

    public DaoResults findAllSkus(QueryInput queryInput, String... joins) {
        BaseDao<BellSku> iiDao = new BaseDao<BellSku>(BellSku.class);
        return iiDao.findAll(queryInput, joins);
    }

    /*
    public BellInventory findByIsbnCond(String isbn, String bellcondition, String... joins) throws NoResultException {
        BaseDao<BellInventory> iiDao = new BaseDao<BellInventory>(BellInventory.class);
        Criteria crit = iiDao.getSession().createCriteria(BellInventory.class);
        crit.add(Restrictions.eq("isbn", isbn));
        crit.add(Restrictions.eq("bellcondition", bellcondition));
        List<BellInventory> list = crit.list();
        if (list.size() > 1){
            logger.error("There is more than one BellInventory for isbn: "+isbn+" bellcondition: "+bellcondition);
        }
        if (list.size() > 0) return list.get(0);
        return null;
    }
    */
     
    public BellInventory findByIsbn(String isbn, String... joins) throws NoResultException {
        BaseDao<BellInventory> iiDao = new BaseDao<BellInventory>(BellInventory.class);
        Criteria crit = iiDao.getSession().createCriteria(BellInventory.class);
        crit.add(Restrictions.eq("isbn", isbn));
        List<BellInventory> list = crit.list();
        if (list.size() > 1){
            logger.error("There is more than one BellInventory for isbn: "+isbn);
        }
        if (list.size() > 0) return list.get(0);
        return null;
    }
     
    public void recalculateCommitted(Long id) {
        BaseDao<BellInventory> iiDao = new BaseDao<BellInventory>(BellInventory.class);
        iiDao.getSession().createSQLQuery("select recalculateBellInvCommitted("+id+")").list();
    }
    
}
