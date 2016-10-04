/*
 * Copyright 2005 Book Country
 *
 * Created on Apr 16, 2005
 *
 */
package com.bc.jasper;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;

/**
 * Data source provider implementation that
 * provides a bean collection data source
 * containing instances of CustomerOrder class.
 */
public class ReceivingDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.jasper.ReceivingDataSourceProvider");

    private Received received;
    private ArrayList<ReceivedItem> items;
    private ReceivedItem item;
    private NumberFormat nf = NumberFormat.getInstance();

    public ReceivingDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
        String name = field.getName();
        if (name.equals("quantity")){
            if (item.getQuantity() == null){
                return new Integer(0);
            }
            return item.getQuantity();
        } else if (name.equals("ISBN")){
            return item.getIsbn();
        } else if (name.equals("condition")){
            return item.getCond();
        } else if (name.equals("title")){
            return item.getTitle();
        } else if (name.equals("bin")){
            return item.getBin();
        } else if (name.equals("unitCost")){
            return "$"+nf.format(item.getCost());
        } else if (name.equals("askingPrice")){
            return "$"+nf.format(item.getSellPrice());
        } else if (name.equals("extendedCost")){
            return "$"+nf.format(item.getExtendedCost());
        }
        return null;
    }

    public boolean next() throws JRException {
        if (items.size() > 0){
            item = items.remove(0);
            return true;
        }
        return false;
    }

    public void setup(Received received) {
        this.received = received;
        items = new ArrayList(received.getReceivedItems());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }


}