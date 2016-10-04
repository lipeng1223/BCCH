<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <tr>
                <td align="right" nowrap valign="top">Vendor Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="vendor.vendorName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="vendor.code"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Account Number:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="vendor.accountNumber"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipping Company:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="vendor.shippingCompany"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Types (<s:property value="vendor.vendorSkidTypes.size()" default="0"/>):</td>
                <s:if test="isBcVendorAdmin">
                <td align="left" style="padding-left:5px;" nowrap>
                    <a href="" onclick="javascript:createSkidType(<s:property value="vendor.id"/>);return false;" style="text-decoration:none;" title="Add New Skid Type"><img src="/images/add.png"/></a>
                </td>
                </s:if>
            </tr>
            <tr style="padding-left:10px;">
                <td align="left" style="padding-left:5px;" nowrap colspan="2">
                    <s:iterator value="vendor.vendorSkidTypesOrdered" var="st">
                        <table style="margin-top:15px;">
                            <tr>
                                <s:if test="isBcVendorAdmin">
                                <td nowrap>
                                    <a href="" onclick="javascript:editSkidType(<s:property value="#st.id"/>, '<s:property value="escapeJavaScript(#st.skidtype)"/>');return false;" style="text-decoration:none;" title="Edit"><img src="/images/pencil.png"/></a>
                                    <a href="" onclick="javascript:deleteSkidType(<s:property value="#st.id"/>);return false;" style="margin-left:8px;text-decoration:none;" title="Delete"><img src="/images/delete.png"/></a>
                                </td>
                                </s:if>
                                <td style="padding-left:10px;"><s:property value="#st.skidtype"/></td>
                            </tr>
                        </table>
                    </s:iterator>
                </td>
            </tr>        
        </table>
    </div>
</div>