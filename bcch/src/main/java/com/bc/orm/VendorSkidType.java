package com.bc.orm;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="vendor_skid_type")
@Inheritance(strategy=InheritanceType.JOINED)
public class VendorSkidType extends BaseEntity implements Auditable, Serializable, Comparable<VendorSkidType> {

    private Vendor vendor;
    private String skidtype;

    public VendorSkidType() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("skidtype", skidtype));
        return sb.toString();
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="vendor_id")
    public Vendor getVendor() {
        return this.vendor;
    }
    
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
    
    @Column(name="skidtype", length=50)
    public String getSkidtype() {
        return this.skidtype;
    }
    
    public void setSkidtype(String skidtype) {
        this.skidtype = skidtype;
    }

    @Transient
    @Override
    public int compareTo(VendorSkidType vst) {
        return this.id.compareTo(vst.getId());
    }

}


