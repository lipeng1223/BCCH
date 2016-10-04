<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<script language="JavaScript" type="text/javascript">

Ext.onReady(function(){
    Ext.auditWindow.getBottomToolbar().addFill();
    Ext.auditWindow.getBottomToolbar().add({
        id:'cancelButton', 
        cls:"x-btn-text-icon", 
        icon:"/images/cancel.png", 
        text:'Close', 
        handler: function(){
            Ext.auditWindow.close();
        }, 
        disabled:false
    });
});
</script>

<form class="formular">
    <fieldset>
        <legend><s:if test="auditTitle != null && auditTitle.length() > 0"><s:property value="auditTitle"/></s:if><s:else>Audit History</s:else></legend>

        <s:if test="daoResults == null || daoResults.data == null || daoResults.data.size() == 0">
            <div style="padding:10px;">
            <img src="/images/error.png"/> There are no Audit records available.
            </div>
        </s:if>        
        <table>
            <s:iterator value="daoResults.data" var="audit" status="astat">
                <tr>
                    <td align="right" nowrap valign="top"><span class="bluetext"><s:property value="#astat.index+1"/>.</span> Audit Time:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="#audit.auditTime" format="MM/dd/yyyy hh:mm a"/></td>
                    
                    <td align="right" nowrap valign="top" style="padding-left:40px;">User:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#audit.username"/></td>
                </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Audit Trail:</td>
                    <td align="left" style="padding-left:25px;" nowrap colspan="3" valign="top">
                        <table>
                            <s:set name="auditChanges" value="#audit.changeCount"/>
                            <s:iterator status="stat" value="{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}" >
                                <s:if test="#stat.index < #auditChanges-1">
                                    <tr>
                                        <td align="right" nowrap valign="top"><s:property value="#audit.getColumnName(#stat.index+1)"/>:</td>
                                        <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3" valign="top"><s:property value="#audit.getPreviousValue(#stat.index+1)"/></td>
                                        <td align="left" style="padding-left:5px;" nowrap colspan="3" valign="top">changed to:</td>
                                        <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3" valign="top"><s:property value="#audit.getCurrentValue(#stat.index+1)"/></td>
                                    </tr>
                                </s:if>
                            </s:iterator>
                        </table>
                    </td>
                </tr>
                <tr><td><div style="height:20px;"></div></td></tr>
            </s:iterator>
        </table>
        <s:if test="childrenResults.data.size() == 200 ">
            <div style="margin-top:10px;">Only showing the latest 200 history items.</div> 
        </s:if>
    </fieldset>
    
    <s:if test="childrenResults != null">
    <fieldset style="margin-top:10px;">
        <legend><s:if test="childAuditTitle != null && childAuditTitle.length() > 0"><s:property value="childAuditTitle"/></s:if><s:else>Children Audit History</s:else></legend>

        <s:if test="childrenResults.data == null || childrenResults.data.size() == 0">
            <div style="padding:10px;">
            <img src="/images/error.png"/> There are no Audit records available.
            </div>
        </s:if>        
        <table>
            <s:iterator value="childrenResults.data" var="audit" status="astat">
                <tr>
                    <td align="right" nowrap valign="top"><span class="bluetext"><s:property value="#astat.index+1"/>.</span> Audit Time:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="#audit.auditTime" format="MM/dd/yyyy hh:mm a"/></td>
                    
                    <td align="right" nowrap valign="top" style="padding-left:40px;">User:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#audit.username"/></td>
                </tr>
                    
                    <tr>
                    <td align="right" nowrap valign="top" style="padding-left:40px;">Action:</td>
                    <s:if test="#audit.auditAction.equals('create')">
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3">Create</td>
                    </s:if>
                    <s:elseif test="#audit.auditAction.equals('delete')">
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3">Delete</td>
                    </s:elseif>
                    <s:else>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3">Edit</td>
                    </s:else>
                    </tr>
                    <tr>
                    <td align="right" nowrap valign="top" style="padding-left:40px;">Message:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3"><s:property value="#audit.auditMessage"/></td>
                    </tr>
                <tr><td><div style="height:5px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Audit Trail:</td>
                    <td align="left" style="padding-left:25px;" nowrap colspan="3" valign="top">
                        <table>
                            <s:set name="auditChanges" value="#audit.changeCount"/>
                            <s:iterator status="stat" value="{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}" >
                                <s:if test="#stat.index < #auditChanges-1">
                                    <tr>
                                        <td align="right" nowrap valign="top"><s:property value="#audit.getColumnName(#stat.index+1)"/>:</td>
                                        <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3" valign="top"><s:property value="#audit.getPreviousValue(#stat.index+1)"/></td>
                                        <td align="left" style="padding-left:5px;" nowrap colspan="3" valign="top">changed to:</td>
                                        <td align="left" style="padding-left:5px;" class="bluetext" nowrap colspan="3" valign="top"><s:property value="#audit.getCurrentValue(#stat.index+1)"/></td>
                                    </tr>
                                </s:if>
                            </s:iterator>
                        </table>
                    </td>
                </tr>
                <tr><td><div style="height:20px;"></div></td></tr>
            </s:iterator>
        </table>
        <s:if test="childrenResults.data.size() == 200 ">
            <div style="margin-top:10px;">Only showing the latest 200 history items.</div> 
        </s:if>
    </fieldset>
    </s:if>
</form>
   