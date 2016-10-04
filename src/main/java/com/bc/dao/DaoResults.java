package com.bc.dao;

import java.util.HashMap;
import java.util.List;


public class DaoResults {
    
    private List data;
    private List dataRanking;
    private String searchString;
    private Integer firstResult;
    private Integer maxResults;
    private Integer totalRecords;
    private HashMap<String, Object> summary;

    public DaoResults() { 
        this(null);
    }
    public DaoResults(List data) {
        super();
        this.data = data;
    }
    
    public List getData() {
        return data;
    }
    public void setData(List data) {
        this.data = data;
    }
    public Integer getDataSize(){
        return data.size();
    }
    public Integer getFirstResult() {
        return firstResult;
    }
    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }
    public Integer getMaxResults() {
        return maxResults;
    }
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
    public Integer getTotalRecords() {
        return totalRecords;
    }
    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
    public HashMap<String, Object> getSummary() {
        return summary;
    }
    public void setSummary(HashMap<String, Object> summary) {
        this.summary = summary;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if (firstResult != null){
            sb.append("\nfirstResult: ");
            sb.append(firstResult.toString());
        }
        if (maxResults != null){
            sb.append("\nmaxResults: ");
            sb.append(maxResults.toString());
        }
        if (totalRecords != null){
            sb.append("\ntotalRecords: ");
            sb.append(totalRecords.toString());
        }
        if (data != null){
            sb.append("\ndata size: ");
            sb.append(data.size());
        }
        if (summary != null){
            sb.append("\nsummary size: ");
            sb.append(summary.size());
        }
        return sb.toString();
    }
    public List getDataRanking() {
        return dataRanking;
    }
    public void setDataRanking(List dataRanking) {
        this.dataRanking = dataRanking;
    }
    public String getSearchString() {
        return searchString;
    }
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

}
