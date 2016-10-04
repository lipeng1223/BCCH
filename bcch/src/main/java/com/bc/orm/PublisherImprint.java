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
@Table(name="publisher_imprint")
@Inheritance(strategy=InheritanceType.JOINED)
public class PublisherImprint extends BaseEntity implements Serializable {

    private Publisher publisher;
    private String name;

    public PublisherImprint() {
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="publisher_id")
    public Publisher getPublisher() {
        return this.publisher;
    }
    
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }




}


