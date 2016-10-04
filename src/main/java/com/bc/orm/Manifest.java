package com.bc.orm;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="manifest")
@Inheritance(strategy=InheritanceType.JOINED)
public class Manifest extends BaseEntity implements Auditable, Serializable {

    private String comment;
    private String name;
    private Date date;
    private Integer totalItems = 0;
    private Integer totalQuantity = 0;
    private Set<ManifestItem> manifestItems = new HashSet<ManifestItem>(0);

    public Manifest() {
    }

    @Transient
    public String getAuditMessage(){
        return "name: "+name;
    }
    
    @Column(name="comment")
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="name", length=50)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="manifest")
    public Set<ManifestItem> getManifestItems() {
        return this.manifestItems;
    }
    
    public void setManifestItems(Set<ManifestItem> manifestItems) {
        this.manifestItems = manifestItems;
    }

    @Column(name="totalitems")
    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    @Column(name="totalquantity")
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

}


