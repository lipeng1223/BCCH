package com.bc.ejb;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.Audit;
import com.bc.struts.QueryInput;

@Local
public interface AuditSessionLocal {

    public abstract Audit findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
}
