package com.bc.orm;


import java.io.Serializable;
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
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="publisher")
@Inheritance(strategy=InheritanceType.JOINED)
public class Publisher extends BaseEntity implements Serializable {

    private String name;
    private Set<PublisherImprint> publisherImprints = new HashSet<PublisherImprint>(0);

    public Publisher() {
    }

    @Transient
    public String getAuditMessage(){
        return "name: "+name;
    }
    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="publisher")
    public Set<PublisherImprint> getPublisherImprints() {
        return this.publisherImprints;
    }
    
    public void setPublisherImprints(Set<PublisherImprint> publisherImprints) {
        this.publisherImprints = publisherImprints;
    }




}


