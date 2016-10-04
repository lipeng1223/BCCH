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

/*
 * 
 * create table fast_received_item (id bigint not null auto_increment, versionbc bigint, lastUpdate datetime, lastUpdateBy varchar(255), createTimeBc datetime, cond varchar(50), isbn varchar(50), received_id bigint, primary key (id));
 * alter table fast_received_item add index FK355A280EE0B9F6DE (received_id), add constraint FK355A280EE0B9F6DE foreign key (received_id) references received (id);
 * 
 */

@SuppressWarnings("serial")
@Entity
@Table(name="fast_received_item")
@Inheritance(strategy=InheritanceType.JOINED)
public class FastReceivedItem extends BaseEntity implements Serializable {

    private Received received;
    private String isbn;
    private String cond;
    
    public FastReceivedItem() {
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="received_id")
    public Received getReceived() {
        return this.received;
    }
    
    public void setReceived(Received received) {
        this.received = received;
    }
    

    @Column(name="isbn", length=50)
    public String getIsbn() {
        return this.isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Column(name="cond", length=50)
    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    
}


