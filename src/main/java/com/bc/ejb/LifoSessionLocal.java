package com.bc.ejb;

import com.bc.dao.BaseDao;
import com.bc.orm.BellReceivedItem;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.bc.orm.ReceivedItem;

@Local
public interface LifoSessionLocal {

    public abstract List<Long> postOrder(Long id, String username, Date postDate) throws Exception;
    public abstract List<Long> unpostOrder(Long id) throws Exception;
    public abstract void createReceivedItem(ReceivedItem item, Long inventoryItemId);
    public abstract void createReceivedItem(ReceivedItem item, Long inventoryItemId, BaseDao<ReceivedItem> riDao, boolean updateBins);
    public abstract void createReceivedItems(List<ReceivedItem> items, List<Long> ids);
    public abstract void updateReceivedItem(ReceivedItem item);
    public abstract void updateReceivedItems(List<ReceivedItem> items);
    public abstract void deleteReceivedItem(ReceivedItem item);
    public abstract void deleteReceivedItems(List<ReceivedItem> items);    
    
    public abstract void createBellReceivedItem(BellReceivedItem item, Long inventoryItemId);
    public abstract void createBellReceivedItems(List<BellReceivedItem> items, List<Long> ids);
    public abstract void updateBellReceivedItem(BellReceivedItem item);
    public abstract void updateBellReceivedItems(List<BellReceivedItem> items);
    public abstract void updatePendingOrderBins(Long iiId, String bin, String title);
    public abstract void deleteBellReceivedItem(BellReceivedItem item);
    public abstract void deleteBellReceivedItems(List<BellReceivedItem> items);    
    public abstract List<Long> postBellOrder(Long id, String username, Date postDate) throws Exception;
    
}
