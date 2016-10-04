/*
 * Copyright 2006 Book Country Clearing House
 *
 * Created - Apr 29, 2006
 */
package com.bc.util;

import java.util.HashSet;

/**
 * @author Tim
 */
public class AlleghenyCountyZipCodes {

    public static HashSet<String> zipCodes;
    
    private AlleghenyCountyZipCodes(){}
    
    public static boolean in(String zip, String city, String country){
        if (country != null && !country.toLowerCase().equals("us") && !country.toLowerCase().equals("usa") && !country.toLowerCase().equals("u.s.a") && !country.toLowerCase().equals("u.s.a.")){
            return false;
        }
        if (city != null && city.toLowerCase().equals("pittsburgh")){
            return true;
        }
        return zipCodes.contains(zip);
    }
    
    static {
        zipCodes = new HashSet<String>();
        zipCodes.add("15006");
        zipCodes.add("15007");
        zipCodes.add("15014");
        zipCodes.add("15015");
        zipCodes.add("15017");
        zipCodes.add("15018");
        zipCodes.add("15020");
        zipCodes.add("15024");
        zipCodes.add("15025");
        zipCodes.add("15028");
        zipCodes.add("15030");
        zipCodes.add("15031");
        zipCodes.add("15032");
        zipCodes.add("15034");
        zipCodes.add("15035");
        zipCodes.add("15037");
        zipCodes.add("15044");
        zipCodes.add("15045");
        zipCodes.add("15046");
        zipCodes.add("15047");
        zipCodes.add("15049");
        zipCodes.add("15051");
        zipCodes.add("15056");
        zipCodes.add("15064");
        zipCodes.add("15065");
        zipCodes.add("15071");
        zipCodes.add("15075");
        zipCodes.add("15076");
        zipCodes.add("15082");
        zipCodes.add("15084");
        zipCodes.add("15086");
        zipCodes.add("15088");
        zipCodes.add("15090");
        zipCodes.add("15091");
        zipCodes.add("15095");
        zipCodes.add("15101");
        zipCodes.add("15102");
        zipCodes.add("15104");
        zipCodes.add("15106");
        zipCodes.add("15108");
        zipCodes.add("15110");
        zipCodes.add("15112");
        zipCodes.add("15116");
        zipCodes.add("15120");
        zipCodes.add("15122");
        zipCodes.add("15123");
        zipCodes.add("15126");
        zipCodes.add("15127");
        zipCodes.add("15129");
        zipCodes.add("15130");
        zipCodes.add("15131");
        zipCodes.add("15132");
        zipCodes.add("15133");
        zipCodes.add("15134");
        zipCodes.add("15135");
        zipCodes.add("15136");
        zipCodes.add("15137");
        zipCodes.add("15139");
        zipCodes.add("15140");
        zipCodes.add("15142");
        zipCodes.add("15143");
        zipCodes.add("15144");
        zipCodes.add("15145");
        zipCodes.add("15146");
        zipCodes.add("15147");
        zipCodes.add("15148");
        zipCodes.add("15201");
        zipCodes.add("15202");
        zipCodes.add("15203");
        zipCodes.add("15204");
        zipCodes.add("15205");
        zipCodes.add("15206");
        zipCodes.add("15207");
        zipCodes.add("15208");
        zipCodes.add("15209");
        zipCodes.add("15210");
        zipCodes.add("15211");
        zipCodes.add("15212");
        zipCodes.add("15213");
        zipCodes.add("15214");
        zipCodes.add("15215");
        zipCodes.add("15216");
        zipCodes.add("15217");
        zipCodes.add("15218");
        zipCodes.add("15219");
        zipCodes.add("15220");
        zipCodes.add("15221");
        zipCodes.add("15222");
        zipCodes.add("15223");
        zipCodes.add("15224");
        zipCodes.add("15225");
        zipCodes.add("15226");
        zipCodes.add("15227");
        zipCodes.add("15228");
        zipCodes.add("15229");
        zipCodes.add("15230");
        zipCodes.add("15231");
        zipCodes.add("15232");
        zipCodes.add("15233");
        zipCodes.add("15234");
        zipCodes.add("15235");
        zipCodes.add("15236");
        zipCodes.add("15237");
        zipCodes.add("15238");
        zipCodes.add("15239");
        zipCodes.add("15240");
        zipCodes.add("15241");
        zipCodes.add("15242");
        zipCodes.add("15243");
        zipCodes.add("15244");
        zipCodes.add("15250");
        zipCodes.add("15251");
        zipCodes.add("15252");
        zipCodes.add("15253");
        zipCodes.add("15254");
        zipCodes.add("15255");
        zipCodes.add("15256");
        zipCodes.add("15257");
        zipCodes.add("15258");
        zipCodes.add("15259");
        zipCodes.add("15260");
        zipCodes.add("15261");
        zipCodes.add("15262");
        zipCodes.add("15263");
        zipCodes.add("15264");
        zipCodes.add("15265");
        zipCodes.add("15266");
        zipCodes.add("15267");
        zipCodes.add("15268");
        zipCodes.add("15270");
        zipCodes.add("15272");
        zipCodes.add("15274");
        zipCodes.add("15275");
        zipCodes.add("15276");
        zipCodes.add("15277");
        zipCodes.add("15278");
        zipCodes.add("15279");
        zipCodes.add("15281");
        zipCodes.add("15282");
        zipCodes.add("15283");
        zipCodes.add("15285");
        zipCodes.add("15286");
        zipCodes.add("15289");
        zipCodes.add("15290");
        zipCodes.add("15295");
    }
}
