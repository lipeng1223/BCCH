package com.bc.orm;


import java.io.Serializable;
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
@Table(name="backstock_location")
@Inheritance(strategy=InheritanceType.JOINED)
public class BackStockLocation extends BaseEntity implements Auditable, Serializable {

    private String location;
    private String row;
    private Integer quantity;
    private String tub;
    
    private BackStockItem backStockItem;

    public BackStockLocation() {
    }

    @Transient
    @Override
    public String getAuditMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append(getColAudit("location", location));
        sb.append(getColAudit("row", row));
        sb.append(getColAudit("tub", tub));
        sb.append(getColAudit("quantity", quantity));
        return sb.toString();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getTub() {
        return tub;
    }

    public void setTub(String tub) {
        this.tub = tub;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="backStockItem_id")
    public BackStockItem getBackStockItem() {
        return backStockItem;
    }

    public void setBackStockItem(BackStockItem backStockItem) {
        this.backStockItem = backStockItem;
    }


}


