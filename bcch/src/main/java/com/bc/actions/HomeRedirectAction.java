package com.bc.actions;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.bc.util.ActionRole;

@Namespace("")
@Results({
    @Result(name="homeredirect", location="/secure/home.bc", type="redirect")
})
public class HomeRedirectAction extends BaseAction {

    @ActionRole({"WebUser"})
    public String execute(){
        return "homeredirect";
    }
}
