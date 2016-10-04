<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;">
    <div style="padding:8px;">
            <table>
                <tr>
                    <td align="right" nowrap valign="top">Date:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="manifest.date" format="MM/dd/yyyy"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Name:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="manifest.name"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Total Items</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="manifest.totalItems"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Total Quantity:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="manifest.totalQuantity"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Comment:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="manifest.comment"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </table>
    </div>
</div>