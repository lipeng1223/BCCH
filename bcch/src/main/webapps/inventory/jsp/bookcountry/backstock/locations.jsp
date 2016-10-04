<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<s:iterator value="backStockLocations" status="st" var="loc">
<table style="width: 100%; margin-bottom:10px; border: 1px dashed #a5a5a5;">
    <tr><td colspan="2"><div style="height:8px;"></div></td></tr>
    <tr>
        <td align="right" nowrap valign="top" style="width:100px;">Location:</td>
        <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#loc.location"/></td>
    </tr>
    <tr>
        <td align="right" nowrap valign="top" style="width:100px;">Row:</td>
        <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#loc.row"/></td>
    </tr>
    <tr>
        <td align="right" nowrap valign="top" style="width:100px;">Quantity:</td>
        <td align="left" style="padding-left:5px;" class="bluetext" nowrap>
            <input type="text" maxlength="255" id="locQuantity-<s:property value="#st.index"/>" class="text-input" value="<s:property value="#loc.quantity"/>">
            <input type="hidden" id="locIds-<s:property value="#st.index"/>" value="<s:property value="#loc.id"/>">
        </td>
    </tr>
    <tr>
        <td align="right" nowrap valign="top" style="width:100px;">Tub:</td>
        <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#loc.tub"/></td>
    </tr>
    <tr><td colspan="2"><div style="height:8px;"></div></td></tr>
</table>
</s:iterator>

