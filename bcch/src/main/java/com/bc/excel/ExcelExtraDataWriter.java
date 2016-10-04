package com.bc.excel;

//import jxl.write.WritableSheet;
import org.apache.poi.ss.usermodel.Sheet;

public interface ExcelExtraDataWriter {

//    public abstract int writeExtraPreData(int row, WritableSheet sheet);
//    public abstract void writeExtraPostData(int row, WritableSheet sheet);
    public abstract int writeExtraPreData(int row, Sheet sheet);
    public abstract void writeExtraPostData(int row, Sheet sheet);
}
