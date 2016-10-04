package com.bc.ejb;


import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.Audit;
import com.bc.struts.QueryInput;

@Stateless
public class AuditSession implements AuditSessionLocal {

    public static final String LocalJNDIString = "inventory/"+AuditSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = AuditSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(AuditSession.class);

    public Audit findById(Long id, String... joins) throws NoResultException {
        BaseDao<Audit> dao = new BaseDao<Audit>(Audit.class);
        return dao.findById(id, joins);
    }
    
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<Audit> dao = new BaseDao<Audit>(Audit.class);
        return dao.findAll(queryInput, joins);
    }

    
}
