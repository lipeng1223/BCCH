package com.bc.orm;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.bc.util.IsbnUtil;

@SuppressWarnings("serial")
@Entity
@Table(name="manifest_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class ManifestItem extends BaseEntity implements Auditable, Serializable, Comparable<ManifestItem> {

    private Manifest manifest;
    private String isbn;
    private String cond;
    private String bin;
    private String isbn13;
    private String title;
    private Integer quantity = 0;
    private Date date;

    public ManifestItem() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("isbn", isbn));
        sb.append(getColAudit("isbn13", isbn13));
        sb.append(getColAudit("condition", cond));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }
    

    @Override
    @Transient
    public int compareTo(ManifestItem mi) {
        if (mi == null) return 0;
        if (title != null && mi.getTitle() != null){
            return title.compareTo(mi.getTitle());
        }
        return 0;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="manifest_id")
    public Manifest getManifest() {
        return this.manifest;
    }
    
    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }
    
    @Column(name="isbn", length=64)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    @Column(name="cond", length=64)
    public String getCond() {
        return this.cond;
    }
    
    public void setCond(String cond) {
        this.cond = cond;
    }
    
    @Column(name="isbn13", length=13)
    public String getIsbn13() {
        return this.isbn13;
    }
    
    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Column(name="quantity")
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date", length=19)
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    @Transient
    public void fixIsbn(){
        if (isbn13 != null && isbn13.length() == 13){
            isbn = IsbnUtil.getIsbn10(isbn13);
        } else if (isbn != null && isbn.length() == 10){
            isbn13 = IsbnUtil.getIsbn13(isbn);
        }
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }
}


