package com.bc.ejb;


import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;

import com.bc.dao.BaseDao;
import com.bc.dao.DaoResults;
import com.bc.orm.Vendor;
import com.bc.orm.VendorSkidType;
import com.bc.struts.QueryInput;
import com.bc.util.cache.VendorCache;

@Stateless
public class VendorSession implements VendorSessionLocal {

    public static final String LocalJNDIString = "inventory/"+VendorSession.class.getSimpleName()+"/local";
    public static final String LocalJNDIStringNoLoader = VendorSession.class.getSimpleName()+"/local";

    private static Logger logger = Logger.getLogger(VendorSession.class);

    public void initCache() {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        Criteria crit = dao.getSession().createCriteria(Vendor.class);
        for (Vendor vendor : (List<Vendor>)crit.list()){
            VendorCache.put(vendor);
        }
    }
    
    public Integer getCount() {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        return dao.getCount();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Vendor findById(Long id) throws NoResultException {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        return dao.findById(id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Vendor findById(Long id, String... joins) throws NoResultException {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        return dao.findById(id, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Vendor vendor) {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        dao.create(vendor, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Vendor vendor) {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        dao.update(vendor, null);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Long id) {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        dao.delete(dao.findById(id), null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public DaoResults findAll(QueryInput queryInput, String... joins) {
        BaseDao<Vendor> dao = new BaseDao<Vendor>(Vendor.class);
        return dao.findAll(queryInput, joins);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(VendorSkidType vendorSkidType) {
        BaseDao<VendorSkidType> dao = new BaseDao<VendorSkidType>(VendorSkidType.class);
        dao.create(vendorSkidType, vendorSkidType.getVendor().getId());
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(VendorSkidType vendorSkidType) {
        BaseDao<VendorSkidType> dao = new BaseDao<VendorSkidType>(VendorSkidType.class);
        dao.update(vendorSkidType, vendorSkidType.getVendor().getId());
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteVendorSkidType(Long id) {
        BaseDao<VendorSkidType> dao = new BaseDao<VendorSkidType>(VendorSkidType.class);
        VendorSkidType vst = dao.findById(id);
        dao.delete(vst, vst.getVendor().getId());
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public VendorSkidType findVendorSkidTypeById(Long id)  throws NoResultException {
        BaseDao<VendorSkidType> dao = new BaseDao<VendorSkidType>(VendorSkidType.class);
        return dao.findById(id);
    }
    
}
