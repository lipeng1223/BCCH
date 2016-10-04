package com.bc.ejb;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.User;
import com.bc.orm.UserRole;
import com.bc.struts.QueryInput;

@Local
public interface UserSessionLocal {

    public abstract Integer getCount();
    public abstract User findById(Long id) throws NoResultException;
    public abstract User findByPin(Integer pin) throws NoResultException;
    public abstract User findByName(String name, String... joins) throws NoResultException;
    public abstract User findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    
    public abstract void create(User user);
    public abstract void update(User user);
    public abstract void delete(Long id);
    
    public abstract void deleteRole(UserRole userRole);
}
