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
        </table>
    </div>
</div>