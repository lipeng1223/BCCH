package com.bc.actions.bookcountry;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.bc.actions.BaseAction;
import com.bc.dao.DaoResults;
import com.bc.ejb.InventoryItemSessionLocal;
import com.bc.jasper.MarketingDataSourceProvider;
import com.bc.orm.InventoryItem;
import com.bc.struts.QueryInput;
import com.bc.struts.result.JasperResult;
import com.bc.util.ActionRole;
import com.bc.util.IsbnUtil;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("serial")
@ParentPackage("bcpackage")
@Namespace("/secure/bookcountry")
@Results({
    @Result(name="tools", location="/WEB-INF/jsp/bookcountry/inventoryitem/tools.jsp"),
    @Result(name="marketing", location="/WEB-INF/jsp/bookcountry/inventoryitem/marketing.jsp"),
    @Result(name="status", location="/WEB-INF/jsp/status.jsp")
})
public class InventoryToolsAction extends BaseAction {

    private static Logger logger = Logger.getLogger(InventoryToolsAction.class);
    
    private String multiIsbn;
    private String bin;
    private String sellPrice;
    private String bcCategory;
    private String restricted;
    private String bellbook;
    private String higherEducation;
    
    private File upload;//The actual file
    private String uploadContentType; //The content type of the file
    private String uploadFileName; //The uploaded file name    
    
    private HashMap paramMap;
    
    @ActionRole({"BcInvAdmin"})
    public String execute(){
        return "tools";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String update(){
        try {
            if (multiIsbn != null && multiIsbn.length() > 0){
                Disjunction dis = Restrictions.disjunction();
                StringTokenizer st = new StringTokenizer(multiIsbn); 
                boolean added = false;
                while (st.hasMoreTokens()){
                    String token = st.nextToken();
                    if (IsbnUtil.isValid(token)){
                        if (IsbnUtil.isValid10(token)) {
                            dis.add(Restrictions.eq("isbn", token));
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(token)));
                        } else {
                            dis.add(Restrictions.eq("isbn", token));
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(token)));
                        }
                    } else {
                        dis.add(Restrictions.eq("isbn", token));
                    }
                    added = true;
                }
                if (!added){
                    setSuccess(false);
                    setMessage("There were no valid ISBN's entered.");
                    return "status";
                }
                queryInput = new QueryInput();
                queryInput.addAndCriterion(dis);
                
                InventoryItemSessionLocal iiSession = getInventoryItemSession();
                DaoResults results = iiSession.findAll(queryInput);
                for (InventoryItem ii : (List<InventoryItem>)results.getData()){
                    boolean update = false;
                    if (bin != null && bin.trim().length() > 0){
                        ii.setBin(bin.trim());
                        update = true;
                    }
                    if (sellPrice != null && sellPrice.trim().length() > 0){
                        try {
                            ii.setSellingPrice(new Float(sellPrice));
                            update = true;
                        } catch (Exception e){}
                    }
                    if (bcCategory != null && bcCategory.trim().length() > 0){
                        ii.setBccategory(bcCategory.trim());
                        update = true;
                    }
                    if (restricted != null){
                        if (restricted.equals("True")) {
                            ii.setRestricted(true);
                            update = true;
                        } else if (restricted.equals("False")) {
                            ii.setRestricted(false);
                            update = true;
                        }
                    }
                    if (bellbook != null){
                        if (bellbook.equals("True")) {
                            ii.setBellbook(true);
                            update = true;
                        } else if (bellbook.equals("False")) {
                            ii.setBellbook(false);
                            update = true;
                        }
                    }
                    if (higherEducation != null){
                        if (higherEducation.equals("True")) {
                            ii.setHe(true);
                            update = true;
                        } else if (higherEducation.equals("False")) {
                            ii.setHe(false);
                            update = true;
                        }
                    }
                    
                    if (update) iiSession.update(ii);
                }
            }
            
            setSuccess(true);
        } catch (Exception e){
            logger.error("Could not update the multiisbns", e);
            setSuccess(false);
            setMessage("Could not update the inventory items, there was a system error.");
        }
        return "status";
    }
    
    @ActionRole({"BcInvAdmin"})
    public String upload(){
        if (upload == null){
            setSuccess(false);
            setMessage("You must provide a file to upload.");
            return "status";
        }
        
        // Check File Extension
        int place = uploadFileName.lastIndexOf( '.' );
        if ( place >= 0 ) {           
            String ext = uploadFileName.substring( place + 1 );
            if(!ext.toLowerCase().equals("xlsx")) {
                setSuccess(false);
                setMessage("Only xlsx files are supported at this time.");
                return "status";
            }
        }
        
        return "status";
    }
    
    
    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String marketing(){
        return "marketing";
    }

    @ActionRole({"BcInvAdmin", "BcInvViewer"})
    public String generateMarketing(){
        try {
            if (multiIsbn != null && multiIsbn.length() > 0){
                Disjunction dis = Restrictions.disjunction();
                StringTokenizer st = new StringTokenizer(multiIsbn); 
                boolean added = false;
                while (st.hasMoreTokens()){
                    String token = st.nextToken();
                    if (IsbnUtil.isValid(token)){
                        if (IsbnUtil.isValid10(token)) {
                            dis.add(Restrictions.eq("isbn", token));
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn13(token)));
                        } else {
                            dis.add(Restrictions.eq("isbn", token));
                            dis.add(Restrictions.eq("isbn", IsbnUtil.getIsbn10(token)));
                        }
                    } else {
                        dis.add(Restrictions.eq("isbn", token));
                    }
                    added = true;
                }
                if (!added){
                    setSuccess(false);
                    setMessage("There were no valid ISBN's entered.");
                    return "status";
                }
                queryInput = new QueryInput();
                queryInput.addAndCriterion(dis);
                
                InventoryItemSessionLocal iiSession = getInventoryItemSession();
                DaoResults results = iiSession.findAll(queryInput);
                List<InventoryItem> items = (List<InventoryItem>)results.getData();

                MarketingDataSourceProvider datasource = new MarketingDataSourceProvider();
                datasource.setup(items);

                setJasperDatasource(datasource);
                setParamMap();
                setJasperParamMap(this.paramMap);
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                setJasperReportName("marketing.jasper");
                setJasperFilename("marketing-"+sdf.format(Calendar.getInstance().getTime())+".pdf");
                setJasperExportType(JasperResult.PDF);

                return "jasperreport";
            }
            setSuccess(false);
            setMessage("There were no valid ISBN's entered.");
            return "status";
        } catch (Throwable t){
            logger.error("Exception generating marketing pdf", t);
            setSuccess(false);
            setMessage("There was a system error and we could not generate the report.");
            return "status";
        }
    }

    public String getMultiIsbn() {
        return multiIsbn;
    }

    public void setMultiIsbn(String multiIsbn) {
        this.multiIsbn = multiIsbn;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getBcCategory() {
        return bcCategory;
    }

    public void setBcCategory(String bcCategory) {
        this.bcCategory = bcCategory;
    }

    public String getRestricted() {
        return restricted;
    }

    public void setRestricted(String restricted) {
        this.restricted = restricted;
    }

    public String getBellbook() {
        return bellbook;
    }

    public void setBellbook(String bellbook) {
        this.bellbook = bellbook;
    }

    public String getHigherEducation() {
        return higherEducation;
    }

    public void setHigherEducation(String higherEducation) {
        this.higherEducation = higherEducation;
    }

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    public String getUploadContentType() {
        return uploadContentType;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }    
    
    protected void setParamMap(){
        paramMap = new HashMap();
        String industryName = System.getProperty("bc.industryName", "Book Country Clearing House");
        
        paramMap.put("industryName", industryName);
    }
}
