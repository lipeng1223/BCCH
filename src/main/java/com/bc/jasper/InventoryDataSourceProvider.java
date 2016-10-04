/*
 * Copyright 2005 Book Country
 *
 * Created on Apr 16, 2005
 *
 */
package com.bc.jasper;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.apache.log4j.Logger;

import com.bc.orm.InventoryItem;
import com.bc.util.DateFormat;

/**
 * Data source provider implementation
 */
public class InventoryDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.InventoryDataSourceProvider");

    private ArrayList<InventoryItem> items;
    private InventoryItem item;
    private HashMap<String,String> methods;
    private HashMap<String, String> titles;
    private HashMap<Integer, byte[]> images;
    private NumberFormat nf = NumberFormat.getInstance();

    public InventoryDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
        String name = field.getName();
        if (methods.containsKey(name)){
            try {
                String method = methods.get(name);
                Method meth = item.getClass().getMethod("get"+method);
                Object ret = meth.invoke(item);
                if (ret != null){
                    if (ret instanceof Double){
                        return nf.format(ret);
                    } else if (ret instanceof Float){
                        return nf.format(ret);
                    } else if (ret instanceof Integer){
                        return ((Integer)ret).toString();
                    } else if (ret instanceof Long) {
                        return ((Long)ret).toString();
                    } else if (ret instanceof Date) {
                        return DateFormat.format((Date)ret);
                    } else if (ret instanceof String){
                        return (String)ret;
                    }
                }
                return "";
            } catch (Exception e){
                logger.error("No method found", e);
            }
        } else if (titles.containsKey(name)){
            return titles.get(name);
        } else if (name.equals("barcode")) {
            try {
                Barcode barcode = BarcodeFactory.createCode128B(item.getIsbn());

                barcode.setBarHeight(15);
                barcode.setBarWidth(1);

                return BarcodeImageHandler.getImage(barcode);
            } catch (Exception e){
                logger.error("Could not create barcode image", e);
            }
        }

        return "";
    }

    public boolean next() throws JRException {
        if (items.size() > 0){
            item = items.remove(0);
            return true;
        }
        return false;
    }

    public void setup(ArrayList<InventoryItem> items,
                       HashMap<String, String> titles,
                       HashMap<String, String> methods,
                       HashMap<Integer, byte[]> images)
    {
        this.items = items;
        this.titles = titles;
        this.methods = methods;
        this.images = images;
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }


}