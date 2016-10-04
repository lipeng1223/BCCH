package com.bc.jasper;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.apache.log4j.Logger;

/**
 * Data source provider implementation
 */
public class BellwetherPackingSlipDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.BellwetherPackingSlipDataSourceProvider");

    private List<BellwetherPackingSlipData> items;
    private BellwetherPackingSlipData item;

    public BellwetherPackingSlipDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
        String name = field.getName();

        if (name.equals("orderNumber")){
            return item.getOrderNumber();
        } else if (name.equals("title")) {
            return item.getTitle();
        } else if (name.equals("sku")) {
            return item.getSku();
        } else if (name.equals("listingId")) {
            if (item.getListingId() == null){
                return "";
            }
            return item.getListingId();
        } else if (name.equals("recipient")) {
            return item.getRecipient();
        } else if (name.equals("location")) {
            return item.getLocation();
        } else if (name.equals("quantity")) {
            return item.getQuantity();
        } else if (name.equals("ship")) {
            return item.getShip();
        } else if (name.equals("buyer")) {
            return item.getBuyer();
        } else if (name.equals("barcode")) {
            try {
                Barcode barcode = BarcodeFactory.createCode128B(item.getOrderNumberNoDashes());

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

    public void setup(List<BellwetherPackingSlipData> items) {
        this.items = items;
    }


}