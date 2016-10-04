package com.bc.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;

import com.bc.orm.BaseEntity;
import com.bc.struts.QueryInput;
import com.bc.util.PropertyAuditLogger;


public class BaseDao <T> {

    public static final String ENTITY_MANAGER_JNDI = "java:InventoryPersistence";
    
    private Logger logger = Logger.getLogger(BaseDao.class);
    
    private Class<?> entityClass;
    
    private BaseDao(){}
    
    public BaseDao(Class<?> entityClass){
        this.entityClass = entityClass;
    }
    
    public EntityManager getEntityManager(){
        try {
            Context ctx = new InitialContext();
            return (EntityManager) ctx.lookup(ENTITY_MANAGER_JNDI);
        } catch (NamingException ne){
            logger.fatal("EntityManager could not be looked up through jndi name: "+ENTITY_MANAGER_JNDI, ne);
        }
        throw new RuntimeException("Could not lookup EntityManager "+ENTITY_MANAGER_JNDI);
    }
    
    public Session getSession() {
        return getSession(getEntityManager());
    }
    
    public Session getSession(EntityManager em){
        if (em.getDelegate() instanceof HibernateEntityManager){
            return ((HibernateEntityManager)em).getSession();
        } else {
            return (Session)em.getDelegate();
        }
    }
    
    public void create(T entity, Long parentOid) {
        getEntityManager().persist(entity);
        PropertyAuditLogger.logAction((BaseEntity)entity, "create", parentOid);
    }

    /*
     * Using merge causes the load of the existing entity into place, which causes child fetching
     * If you use update you do not get the previousState in the Audit interceptor, so we had to do our own reflection to see property changes
     * 
     */
    public void update(T entity, Long parentOid) {
        update(entity, parentOid, true);
    }
    public void update(T entity, Long parentOid, Boolean audit) {
        Session session = getSession();
        
        if (audit) {
            BaseEntity old = (BaseEntity)session.get(entity.getClass(), ((BaseEntity)entity).getId());
            try {
                PropertyAuditLogger.logDiff((BaseEntity)old.clone(), (BaseEntity)entity, parentOid);
            } catch (CloneNotSupportedException cnse){
                // be silent about this
            }
            session.evict(old);
        }
        
        session.update(entity); 
        //getEntityManager().merge(entity);
    }
    
   /*  Playing with a batch updater, it is actually slow when we are coming in from a stateless ejb
    public void batchUpdate(List<T> entities) {
        EntityManager em = getEntityManager();
        Session session = getSession();
        session.setFlushMode(FlushMode.MANUAL);  // manual flush
        for (T entity : entities){
            em.merge(entity);
        }
        session.flush();
        session.clear();
        session.setFlushMode(FlushMode.AUTO); // set it back to auto flush
    }
    */
    
    public void lock(T entity, LockMode lock) {
        getSession().lock(entity, lock);
    }

    public BaseEntity get(BaseEntity entity, LockMode lock) {
        return (BaseEntity)getSession().get(entity.getClass(), entity.getId(), lock);
    }

    public BaseEntity get(Long id, LockMode lock) {
        return (BaseEntity)getSession().get(entityClass, id, lock);
    }
    
    public void refresh(T entity) {
        getSession().refresh(entity);
    }
    
    public void refresh(T entity, LockMode lock) {
        getSession().refresh(entity, lock);
    }
    
    public void delete(T entity, Long parentOid) {
        getEntityManager().remove(entity);
        PropertyAuditLogger.logAction((BaseEntity)entity, "delete", parentOid);
    }
    
    public void flush() {
        getEntityManager().flush();
    }

    public void clear() {
        getEntityManager().clear();
    }

    public void flushAndClear() {
        EntityManager em = getEntityManager();
        em.flush();
        em.clear();
    }
    
    public T findById(Long id) {
        EntityManager em = getEntityManager();
        return (T)em.find(entityClass, id);
    }
    
    public T findById(Long id, String... joins) {
        Criteria crit = getSession().createCriteria(entityClass);
        crit.add(Restrictions.idEq(id));
        for (String j : joins){
            crit.setFetchMode(j, FetchMode.JOIN);
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (T)crit.uniqueResult();
    }
    
    public T findUnique(QueryInput qi, String... joins) {
        Criteria crit = createCriteria(qi, joins);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (T)crit.uniqueResult();
    }

    public Integer getCount() {
        Session session = getSession();
        Criteria crit = session.createCriteria(entityClass);
        crit.setProjection(Projections.rowCount());        
        return (Integer)crit.uniqueResult();
    }
    
    public Integer getCount(Class entity, Criteria crit) {
        Session session = getSession();
        crit.setProjection(Projections.rowCount());        
        return (Integer)crit.uniqueResult();
    }
    
    public Integer getCount(Class entity, Criterion... restrictions) {
        Session session = getSession();
        Criteria crit = session.createCriteria(entity);
        for (Criterion c : restrictions){
            crit.add(c);
        }
        return getCount(entity, crit);
    }
    
    public Criteria createCriteria(QueryInput qi, String... joins) {
        return createCriteria(qi, new HashMap<String, String>(), joins);
    }
    
    public Criteria createCriteria(QueryInput qi, HashMap<String, String> aliases, String... joins) {
        Criteria crit = getSession().createCriteria(entityClass);
        HashSet<String> currentAliases = qi.applyFilterParams(crit, null);
        for (Map.Entry<String, String> entry : aliases.entrySet()){
            if (!currentAliases.contains(entry.getKey())){
                crit.createAlias(entry.getKey(), entry.getValue());
                //crit.createCriteria(entry.getKey(), entry.getValue());
                currentAliases.add(entry.getKey());
                //logger.error("added alias for aliases passed in, alias: "+entry.getKey());
            }
        }
        for (String j : joins){
            crit.setFetchMode(j, FetchMode.JOIN);
            //logger.error("join for: "+j);
        }
        for (Criterion c : qi.getAndCriterions()){
            crit.add(c);
        }
        if (qi.getOrCriterions().size() > 0){
            Disjunction dis = Restrictions.disjunction();
            for (Criterion c : qi.getOrCriterions()){
                dis.add(c);
            }
            crit.add(dis);
        }
        
        if (qi.hasGroupBy()){
            //logger.error("groupBy: "+qi.getGroupBy());
            Integer position = 0;
            while (position >= 0){
                position = addAlias(crit, qi.getGroupBy(), position, currentAliases);
            }
            crit.addOrder(Order.asc(removeAllButLastDot(qi.getGroupBy())).ignoreCase());
        } 
        
        if (qi.hasSortCol()){
            //logger.error("sortCol: "+qi.getSortCol());
            Integer position = 0;
            while (position >= 0){
                //logger.error("looking for alias: "+position+" "+qi.getSortCol());
                position = addAlias(crit, qi.getSortCol(), position, currentAliases);
                //logger.error("position is now: "+position);
            }
            //logger.error("setting sort on: "+removeAllButLastDot(qi.getSortCol()));
            if (qi.getSortDir().equals(QueryInput.SORT_ASC)){
                crit.addOrder(Order.asc(removeAllButLastDot(qi.getSortCol())).ignoreCase());
            } else {
                crit.addOrder(Order.desc(removeAllButLastDot(qi.getSortCol())).ignoreCase());
            }
        }
        if (qi.hasSortCol2()){
            //logger.error("sortCol2: "+qi.getSortCol2());
            Integer position = 0;
            while (position >= 0){
                //logger.error("looking for alias: "+position+" "+qi.getSortCol());
                position = addAlias(crit, qi.getSortCol2(), position, currentAliases);
                //logger.error("position is now: "+position);
            }
            //logger.error("setting sort on "+qi.getSortCol());
            if (qi.getSortDir2().equals(QueryInput.SORT_ASC)){
                crit.addOrder(Order.asc(removeAllButLastDot(qi.getSortCol())).ignoreCase());
            } else {
                crit.addOrder(Order.desc(removeAllButLastDot(qi.getSortCol())).ignoreCase());
            }
        }
        
        return crit;
    }
    
    private String removeAllButLastDot(String str){
        int pos = str.lastIndexOf(".");
        if (pos > 0){
            StringBuilder sb = new StringBuilder();
            sb.append(str.substring(0, pos).replace(".", ""));
            sb.append(str.substring(pos));
            return sb.toString();
        }
        return str;
    }
    
    private Integer addAlias(Criteria crit, String col, Integer position, HashSet<String> currentAliases){
        Integer first = col.indexOf(".", position);
        if (first < 0) return first;
        String alias = col.substring(0, first);
        //logger.error("currentAliases.contains "+alias+" : "+currentAliases.contains(alias));
        if (!currentAliases.contains(alias)){
            crit.createAlias(alias, alias.replace(".", ""));
            currentAliases.add(alias);
            //logger.error("alias added for: "+alias+" : "+alias.replace(".", ""));
        }
        return first+1;
    }
    
    public DaoResults findAll(QueryInput qi, String... joins) {
        return findAll(qi, new HashMap<String, String>(), joins);
    }
    
    public DaoResults findAll(QueryInput qi, HashMap<String, String> aliases, String... joins) {
        DaoResults results = new DaoResults();
        
        // only get the id's that match in case of joins - paging hack
        Integer rowCount = null;
        List<Long> ids = null;
        if (qi.isPagingEnabled()){
            Criteria idCrit = createCriteria(qi, aliases, joins);
            idCrit.setMaxResults(qi.getLimit());
            idCrit.setFirstResult(qi.getStart());
            ProjectionList plist = Projections.projectionList();
            plist.add(Projections.property("id"));
            ids = idCrit.setProjection(Projections.distinct(plist)).list();
            Criteria countCrit = createCriteria(qi, aliases, joins);
            rowCount = (Integer)countCrit.setProjection(Projections.countDistinct("id")).uniqueResult();
        }
        
        // now do the actual query
        Criteria crit = createCriteria(qi, aliases, joins);
        if (qi.isPagingEnabled() && ids.size() > 0){
            crit.add(Restrictions.in("id", ids));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        results.setData(crit.list());
        results.setFirstResult(qi.getStart());
        results.setMaxResults(qi.getLimit());
        if (qi.isPagingEnabled()){
            results.setTotalRecords(rowCount);
        } else {
            results.setTotalRecords((Integer)crit.setProjection(Projections.countDistinct("id")).uniqueResult());
        }
        
        return results;
    }
    
    public Integer executeJdbcUpdate(String jdbcUpdate) {
        return getSession().createSQLQuery(jdbcUpdate).executeUpdate();
    }
    
}