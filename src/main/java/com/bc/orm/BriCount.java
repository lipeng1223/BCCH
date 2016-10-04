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

@SuppressWarnings("serial")
@Entity
@Table(name="bri_count")
@Inheritance(strategy=InheritanceType.JOINED)
public class BriCount extends BaseEntity implements Serializable {

    private Boolean pieces = false;
    private Float countOrLbs;

    private BreakReceivedItem breakReceivedItem;

    public BriCount() {
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bri_id")
    public BreakReceivedItem getBreakReceivedItem() {
        return this.breakReceivedItem;
    }
    
    public void setBreakReceivedItem(BreakReceivedItem breakReceivedItem) {
        this.breakReceivedItem = breakReceivedItem;
    }

    @Column(name="pieces")
    public Boolean getPieces() {
        return pieces;
    }

    public void setPieces(Boolean pieces) {
        this.pieces = pieces;
    }

    @Column(name="countorlbs")
    public Float getCountOrLbs() {
        return countOrLbs;
    }

    public void setCountOrLbs(Float countOrLbs) {
        this.countOrLbs = countOrLbs;
    }

}


