package com.bc.amazon;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.bc.orm.InventoryItem;

/**
 * @author Tim
 */
public class AmazonImage {
/*
    private static Logger logger = Logger
        .getLogger("com.bc.ejb.session.AmazonImage");

    private static byte[] buff = new byte[500000];

    public static void findImage(InventoryItem item){
        BowkerImageDAO dao = new BowkerImageDAO();
        try {
            if (dao.findByIsbn(item.getIsbn()) != null){
                logger.debug("Image already exists in the database");
                return;
            }
        } catch (Exception e){
            logger.error("Trouble looking up bowker image", e);
        }

        try {
            AmazonItemLookupSoap.lookupData(item);
        } catch (Exception e){
            // do nothing
        }

        String url = item.getAmazonLargeImageSrc();
        if (url == null) {
            url = item.getAmazonImageSrc();
        }

        if (url == null){
            return;
        }
        try {
            URLConnection con = new URL(url).openConnection();
            int length = con.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            int offset = 0;
            int read = bis.read(buff, offset, length);
            while (read != -1){
                offset += read;
                read = bis.read(buff, offset, length);
            }
            byte[] image = new byte[offset];
            for (int j = 0; j < image.length; j++){
                image[j] = buff[j];
            }
            // create a bowker Image
            BowkerImage bi = new BowkerImage();
            bi.setIsbn(item.getIsbn());
            bi.setImage(Hibernate.createBlob(image));
            bi.setType("jpeg");
            dao.save(bi);
            logger.debug("Saved image for isbn: "+item.getIsbn());
        } catch (Exception e){
            logger.error("Could not create image", e);
        }
    }
    */
}
