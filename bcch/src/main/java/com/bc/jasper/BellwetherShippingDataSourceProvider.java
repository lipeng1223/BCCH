package com.bc.jasper;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.apache.log4j.Logger;

public class BellwetherShippingDataSourceProvider extends JRAbstractBeanDataSource {

    private static final Logger logger =
        Logger.getLogger("com.bc.util.BellwetherShippingDataSourceProvider");

    private List<BellwetherShippingData> items;
    private HashMap<Integer, BellwetherShippingData> itemMap = new HashMap<Integer, BellwetherShippingData>();

    public BellwetherShippingDataSourceProvider() {
        super(false);
    }

    public void moveFirst() throws JRException {
        // Nothing
    }

    public Object getFieldValue(JRField field) throws JRException {
        String name = field.getName();

        for (int i = 0; i < 10; i++){
            if (name.equals("recipient"+(i+1))) {
                if (itemMap.containsKey(i)){
                    BellwetherShippingData item = itemMap.get(i);
                    return item.getRecipient();
                }
                return "";
            } else if (name.equals("barcode"+(i+1))) {
                if (itemMap.containsKey(i)){
                    try {
                        BellwetherShippingData item = itemMap.get(i);
                        Barcode barcode = BarcodeFactory.createCode128B(item.getOrderid());
                        barcode.setDrawingText(true);
                        barcode.setFont(Font.decode("Dialog 14"));
                        barcode.setBarHeight(50);
                        barcode.setBarWidth(1);
                        return BarcodeImageHandler.getImage(barcode);
                    } catch (Exception e){
                        logger.error("Could not create barcode image", e);
                    }
                }
                return null;
            }
        }

        return null;
    }

    public boolean next() throws JRException {
        itemMap.clear();
        if (items.size() > 0){
            for (int i = 0; i < 10; i++){
                if (items.size() > 0){
                    itemMap.put(i, items.remove(0));
                }
            }
            return true;
        }
        return false;
    }

    public void setup(List<BellwetherShippingData> items) {
        this.items = items;
    }


}