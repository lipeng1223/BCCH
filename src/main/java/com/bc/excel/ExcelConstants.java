package com.bc.excel;

public final class ExcelConstants
{


    /**
     * Character set used by Excel for column names
     */
    public static final String[] EXCEL_COL_NAMES = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M",
        "N","O","P","Q","R","S","T","U","V","W","X","Y","Z" };

    /**
     * The maximum number of rows in an Excel sheet is 65,536 (as designed by Microsoft).
     * See http://support.microsoft.com/?kbid=264626
     */
    public static final int EXCEL_MAX_ROW = 65536;

    /**
     * The maximum number of rows written to a data sheet.  A per-sheet
     * limitation of 65530 hyperlinks was reached during testing.  Additionally, in large workbooks,
     * this will allow some empty rows at the end for adding things.
     */
    public static final int EXCEL_MAX_DATA_ROW = 65400;

    /**
     * The maximum number of columns in an Excel sheet is 256 (as designed by Microsoft).
     * See http://support.microsoft.com/?kbid=264626
     */
    public static final int EXCEL_MAX_COL = 256;

    /**
     * File extension for Excel files
     */
//    public static final String EXCEL_FILE_EXT = ".xls";
    public static final String EXCEL_FILE_EXT = ".xlsx";


    /**
     * Separators
     */
    public static final String RANGE_SEP = ":";
    public static final String RANGE_LIST_SEP = ",";
    public static final String SHEET_COL_SEP = "|";

    /**
     * Property names
     */
    public static final String ID_PROPERTY = "oid";
    public static final String VERSION_PROPERTY = "version";


    /**
     * Constants related to Autofit sheet
     */
    public static final String AUTOFIT_COL_PFX = "Col=";
    public static final String AUTOFIT_ROW_PFX = "Row=";
    public static final String AUTOFIT_SEP = "|";

    /**
     * HTTP response content type and header constants
     */
    public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String CONTENT_TYPE_ZIP = "application/zip";
    public static final String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";
    public static final String CONTENT_DISPOSITION_PFX = "attachment;filename=";
    public static final String CONTENT_DISPOSITION_EXCEL_SFX = ".xlsx";
//    public static final String CONTENT_DISPOSITION_EXCEL_SFX = ".xls";
    public static final String CONTENT_DISPOSITION_ZIP_SFX = ".zip";


}                                    // ExcelConstants