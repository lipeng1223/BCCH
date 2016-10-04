package com.bc.ejb;


import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.Manifest;
import com.bc.orm.ManifestItem;
import com.bc.struts.QueryInput;

@Stateless
public class ManifestSession implements ManifestSessionLocal {

    public static final String LocalJNDIString = "inventory/"+ManifestSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = ManifestSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(ManifestSession.class);
        
    public Integer getCount() {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Manifest findById(Long id) throws NoResultException {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Manifest findById(Long id, String... joins) throws NoResultException {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Manifest manifest) {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        dao.create(manifest, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Manifest manifest) {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        dao.update(manifest, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        return dao.findAll(queryInput, joins);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins) {
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        return dao.findAll(queryInput, aliases, joins);
    }
        
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ManifestItem findManifestItemById(Long id, String... joins) throws NoResultException {
        BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
        return dao.findById(id, joins);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(ManifestItem manifestItem) {
        BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
        dao.create(manifestItem, manifestItem.getManifest().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(ManifestItem manifestItem) {
        BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
        dao.update(manifestItem, manifestItem.getManifest().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteManifestItem(Long id) {
        BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
        ManifestItem mi = dao.findById(id);
        dao.delete(mi, mi.getManifest().getId());
    }
    
    public void updateCounts(Long id){
        BaseDao<Manifest> dao = new BaseDao<Manifest>(Manifest.class);
        StringBuilder sb = new StringBuilder("update manifest as m set m.totalItems = (select count(mi.id) from manifest_item as mi where mi.manifest_id = m.id) where m.id = ");
        sb.append(id);
        dao.getSession().createSQLQuery(sb.toString()).executeUpdate();
        sb = new StringBuilder("update manifest as m set m.totalQuantity = (select sum(mi.quantity) from manifest_item as mi where mi.manifest_id = m.id) where m.id = ");
        sb.append(id);
        dao.getSession().createSQLQuery(sb.toString()).executeUpdate();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAllManifestItems(QueryInput queryInput, String... joins) {
        BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
        return dao.findAll(queryInput, joins);
    }

    public Integer getItemCount(Long manifestId) {
        BaseDao<Manifest> mdao = new BaseDao<Manifest>(Manifest.class);
        Manifest m = mdao.findById(manifestId);
        if (m != null){
            return mdao.getCount(ManifestItem.class, Restrictions.eq("manifest", m));
        }
        return 0;
    }
    
    public Boolean processItemUpload(Long manifestId, List<ManifestItem> items) {
        try {
            BaseDao<ManifestItem> dao = new BaseDao<ManifestItem>(ManifestItem.class);
            BaseDao<Manifest> mdao = new BaseDao<Manifest>(Manifest.class);
            
            HashMap<String, ManifestItem> toupdate = new HashMap<String, ManifestItem>();
            for (ManifestItem mi : items) toupdate.put(mi.getIsbn(), mi);
            
            Manifest manifest = mdao.findById(manifestId, "manifestItems");
            for (ManifestItem emi : manifest.getManifestItems()){
                if (toupdate.containsKey(emi.getIsbn())){
                    ManifestItem mi = toupdate.remove(emi.getIsbn());
                    
                    manifest.setTotalQuantity(manifest.getTotalQuantity()-emi.getQuantity());
                    manifest.setTotalQuantity(manifest.getTotalQuantity()+mi.getQuantity());
                    
                    emi.setQuantity(mi.getQuantity());
                    emi.setTitle(mi.getTitle());
                    dao.update(emi, manifest.getId());
                }
            }
            // finish off the remaining toupdate with creates
            for (String key : toupdate.keySet()){
                ManifestItem mi = toupdate.get(key);
                mi.setManifest(manifest);
                dao.create(mi, manifest.getId());
                manifest.setTotalItems(manifest.getTotalItems()+1);
            }
            
            return true;
        } catch (Exception e){
            logger.error("Could not process the item upload", e);
        }
        return false;
    }
    
}
