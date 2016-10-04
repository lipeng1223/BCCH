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

//import com.bc.orm.SalesOrderItem;

/**
 * Data source provider implementation
 */
public class SalesOrderDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.SalesOrderDataSourceProvider");

    private ArrayList items;
    //private SalesOrderItem item;
    private NumberFormat nf = NumberFormat.getInstance();

    public SalesOrderDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
    	/*
        String name = field.getName();
        try {
            if (name.equals("orderedQuantity")){
                if (item.getQuantity() == null){
                    return new Integer(0);
                }
                return new Integer(item.getQuantity());
            } else if (name.equals("ISBN")){
                return item.getIsbn();
            } else if (name.equals("title")){
                return item.getTitle();
            } else if (name.equals("pubPrice")){
                return "$"+nf.format(new Double(item.getPubPrice()));
            } else if (name.equals("salePrice")){
                return "$"+nf.format(new Double(item.getSellPrice()));
            } else if (name.equals("extended")){
                if (item.getExtended() == null){
                    return "$"+nf.format(new Double(0));
                } else {
                    return item.getExtended();
                }
            }
        } catch (Exception e){
            return null;
        }
        */
        return null;
    }

    public boolean next() throws JRException {
        if (items.size() > 0){
            //item = (SalesOrderItem)items.remove(0);
            return true;
        }
        return false;
    }

    public void setup(ArrayList items) {
        this.items = items;
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }


}