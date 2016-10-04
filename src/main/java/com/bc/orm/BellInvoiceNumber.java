package com.bc.orm;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="bell_invoice_number")
@Inheritance(strategy=InheritanceType.JOINED)
public class BellInvoiceNumber extends BaseEntity implements Serializable {

    public BellInvoiceNumber() {
    }

}


