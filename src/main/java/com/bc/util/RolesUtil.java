package com.bc.util;

import java.util.HashMap;
import java.util.HashSet;

public class RolesUtil {

    private RolesUtil(){}
    
    private static HashMap<String, HashSet<String>> roleMappings = new HashMap<String, HashSet<String>>();
    
    static {
        
        HashSet<String> bcInvAdmin = new HashSet<String>();
        bcInvAdmin.add("/secure/bookcountry/inventory");
        roleMappings.put("BcInvAdmin", bcInvAdmin);
        
    }
}
