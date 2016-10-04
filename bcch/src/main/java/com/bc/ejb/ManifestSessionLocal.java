package com.bc.ejb;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.NoResultException;

import com.bc.dao.DaoResults;
import com.bc.orm.Manifest;
import com.bc.orm.ManifestItem;
import com.bc.struts.QueryInput;

@Local
public interface ManifestSessionLocal {

    public abstract Integer getCount();
    public abstract Manifest findById(Long id) throws NoResultException;
    public abstract Manifest findById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAll(QueryInput queryInput, String... joins);
    public abstract DaoResults findAll(QueryInput queryInput, HashMap<String, String> aliases, String... joins);
    
    public abstract void create(Manifest manifest);
    public abstract void update(Manifest manifest);
    public abstract void delete(Long id);
    
    public abstract Integer getItemCount(Long manifestId);
    public abstract ManifestItem findManifestItemById(Long id, String... joins) throws NoResultException;
    public abstract DaoResults findAllManifestItems(QueryInput queryInput, String... joins);
    
    public abstract void create(ManifestItem manifestItem);
    public abstract void update(ManifestItem manifestItem);
    public abstract void deleteManifestItem(Long id);
    public abstract void updateCounts(Long id);

    public abstract Boolean processItemUpload(Long manifestId, List<ManifestItem> items);
}
