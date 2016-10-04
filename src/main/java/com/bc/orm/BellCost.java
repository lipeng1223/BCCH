package com.bc.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_cost")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellCost extends BaseEntity implements Serializable {

    private String skuprefix;
    private Float cost;

    public BellCost() {
    }

    public BellCost(String skuprefix, Float cost) {
       this.skuprefix = skuprefix;
       this.cost = cost;
    }
   
    @Column(name="skuprefix", length=30)
    public String getSkuprefix() {
        return this.skuprefix;
    }
    
    public void setSkuprefix(String skuprefix) {
        this.skuprefix = skuprefix;
    }
    
    @Column(name="cost", precision=10)
    public Float getCost() {
        return this.cost;
    }
    
    public void setCost(Float cost) {
        this.cost = cost;
    }

}


