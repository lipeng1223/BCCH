package com.bc.orm;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bc.util.IsbnUtil;
import java.util.Set;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="backstock_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class BackStockItem extends BaseEntity implements Auditable, Serializable, Comparable<BackStockItem> {

    private Set<BackStockLocation> backStockLocations;
    private String isbn;
    private String isbn13;
    private String title;
    private Integer totalQuantity = 0;
    private Integer totalLocations = 0;
    private Integer onhand = 0;
    private Integer committed = 0;
    private Integer available = 0;
    private String smallImage;
    private String mediumImage;
    private String comment;

    public BackStockItem() {
    }

    @Transient
    public String getAuditMessage(){
        return "isbn: "+isbn+" isbn13: "+isbn13;
    }

    @Override
    public int compareTo(BackStockItem bi) {
        if (bi == null) return 0;
        if (title != null && bi.getTitle() != null){
            return title.compareTo(bi.getTitle());
        }
        return 0;
    }
    


    public void fixIsbn(){
        if (isbn13 != null && isbn13.length() == 13){
            isbn = IsbnUtil.getIsbn10(isbn13);
        } else if (isbn != null && isbn.length() == 10){
            isbn13 = IsbnUtil.getIsbn13(isbn);
        }
    }

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="backStockItem")
    public Set<BackStockLocation> getBackStockLocations() {
        return backStockLocations;
    }

    public void setBackStockLocations(Set<BackStockLocation> backStockLocations) {
        this.backStockLocations = backStockLocations;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(Integer totalLocations) {
        this.totalLocations = totalLocations;
    }
    
    public Integer getOnhand() {
        return onhand;
    }

    public void setOnhand(Integer onhand) {
        this.onhand = onhand;
    }

    public Integer getCommitted() {
        return committed;
    }

    public void setCommitted(Integer committed) {
        this.committed = committed;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }
    
    public String getMediumImage(){
        return mediumImage;
    }
    
    public void setMediumImage(String mediumImage){
        this.mediumImage = mediumImage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }    
    
}


