package com.bc.ejb;


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
import com.bc.orm.User;
import com.bc.orm.UserRole;
import com.bc.struts.QueryInput;

@Stateless
public class UserSession implements UserSessionLocal {

    public static final String LocalJNDIString = "inventory/"+UserSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = UserSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(UserSession.class);
        
    public Integer getCount() {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User findById(Long id) throws NoResultException {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User findByPin(Integer pin) throws NoResultException {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        Criteria crit = dao.getSession().createCriteria(User.class);
        crit.add(Restrictions.eq("pin", pin));
        crit.setFetchMode("roles", FetchMode.JOIN);
        return (User)crit.uniqueResult();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User findByName(String name, String... joins) throws NoResultException {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        Criteria crit = dao.getSession().createCriteria(User.class);
        crit.add(Restrictions.like("username", name));
        if (joins != null){
            for (String j : joins){
                crit.setFetchMode(j, FetchMode.JOIN);
            }
        }
        return (User)crit.uniqueResult();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User findById(Long id, String... joins) throws NoResultException {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(User user) {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        dao.create(user, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(User user) {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        dao.update(user, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteRole(UserRole userRole) {
        BaseDao<UserRole> dao = new BaseDao<UserRole>(UserRole.class);
        dao.delete(dao.findById(userRole.getId()), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<User> dao = new BaseDao<User>(User.class);
        return dao.findAll(queryInput, joins);
    }

    
}
