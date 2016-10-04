/*
 * Copyright 2005 Book Country
 *
 * Created on Apr 16, 2005
 *
 */
package com.bc.jasper;

import com.bc.amazon.AmazonData;
import com.bc.amazon.AmazonItemLookupSoap;
import java.text.NumberFormat;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

import org.apache.log4j.Logger;

import com.bc.orm.InventoryItem;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 * Data source provider implementation
 */
public class MarketingDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.InventoryDataSourceProvider");

    private List<InventoryItem> items;
    private InventoryItem item;
    private NumberFormat nf = NumberFormat.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("( MMM dd yyyy )");

    public MarketingDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
        String name = field.getName();
        if (name.equals("title")){
            return item.getTitle();
        } else if (name.equals("cover")){
            if (item.getCover() != null && item.getCover().equals("PAP")) return "Paperback";
            if (item.getCover() != null && item.getCover().equals("HC")) return "Hardcover";
            return "";
        } else if (name.equals("isbn10")){
            return item.getIsbn10();
        } else if (name.equals("isbn13")){
            return item.getIsbn13();
        } else if (name.equals("listprice")){
            if (item.getListPrice() != null)
                return "$"+nf.format(item.getListPrice());
            return "";
        } else if (name.equals("price")){
            if (item.getSellingPrice() != null)
                return "$"+nf.format(item.getSellingPrice());
            return "";
        } else if (name.equals("quantity")){
            return ""+item.getAvailable();
        } else if (name.equals("publisher")){
            String p = "";
            if (item.getPublisher() != null) p = item.getPublisher();
            if (item.getPublishDate() != null) p = p+" "+sdf.format(item.getPublishDate());
            return p;
        } else if (name.equals("author")){
            String author = item.getAuthor();
            if (author == null)
                return "";
            return item.getAuthor();
        }else if (name.equals("pages")){
            if (item.getNumberOfPages() != null) return ""+item.getNumberOfPages()+" pages";
            return "";
        } else if (name.equals("imageurl")){
            String strUrl = null;
            if (item.getMediumImage()!= null) strUrl = item.getMediumImage();
            else if (item.getSmallImage() != null) strUrl = item.getSmallImage();
            try{
                URL url = new URL(strUrl);
            } catch(Exception e){
                AmazonData amazonData = AmazonItemLookupSoap.getInstance().lookupAmazonData(item.getIsbn());
                strUrl = amazonData.getLargeImageUrl();
                if (strUrl == null || strUrl == "")
                    strUrl = "http://i.imgur.com/JIT5wWk.png";
            }
            return strUrl;
        } else if (name.equals("dimensions")){
            if (item.getWidth() == null || item.getHeight() == null || item.getLength() == null)
                return "";
            return item.getWidth() + "(w) x " + item.getHeight() + "(h) x " + item.getLength() + "(d)";
        } else if (name.equals("category")){
            String tmp = item.getBccategory();
            if (tmp == null)
                return "";
            return tmp;
        } else if (name.equals("barcode")){
            try {
                Barcode barcode = BarcodeFactory.createCode128(item.getIsbn13());
                barcode.setDrawingText(true);
                barcode.setBarHeight(50);
                return BarcodeImageHandler.getImage(barcode);
            } catch (BarcodeException ex) {
                java.util.logging.Logger.getLogger(MarketingDataSourceProvider.class.getName()).log(Level.SEVERE, null, ex);
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

    public void setup(List<InventoryItem> items) {
        this.items = items;
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }


}