<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;">
    <div style="padding:8px;">
        <div style="height:10px;">&nbsp;</div>
            <table style="width: 100%; margin-bottom:10px;">
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">ISBN:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="backStockItem.isbn"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">ISBN13:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="backStockItem.isbn13"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">Total Quantity:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="backStockItem.totalQuantity"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">Total Locations:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="backStockItem.totalLocations"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">Title:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="backStockItem.title"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top">Image:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext"><s:if test="hasImage"><img src="<s:property value="backStockItem.smallImage"/>"/></s:if><s:else>No Image Data</s:else></td>
                </tr>
            </table>
        
        <a href="" onclick="javascript:addBackStockLocation();return false;" style="text-decoration:none;" title="Add"><img src="/images/add.png"/> Add Location</a>
        <div style="height:10px;">&nbsp;</div>

        <s:iterator value="backStockItem.backStockLocations" status="st" var="loc">
            <table style="width: 100%; margin-bottom:15px; border: 1px dashed #a5a5a5;">
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
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#loc.quantity"/></td>
                </tr>
                <tr>
                    <td align="right" nowrap valign="top" style="width:100px;">Tub:</td>
                    <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#loc.tub"/></td>
                </tr>
                <tr><td colspan="2"><div style="height:4px;"></div></td></tr>
                <tr>
                    <td nowrap colspan="2" style="padding-left:10px;">
                        <a href="" onclick="javascript:editBackStockLocation(<s:property value="#loc.id"/>);return false;" style="text-decoration:none;" title="Edit"><img src="/images/pencil.png"/></a>
                        <a href="" onclick="javascript:deleteBackStockLocation(<s:property value="#loc.id"/>, '<s:property value="#loc.location"/>');return false;" style="margin-left:8px;text-decoration:none;" title="Delete"><img src="/images/delete.png"/></a>
                    </td>
                </tr>
                <tr><td colspan="2"><div style="height:8px;"></div></td></tr>
            </table>
        </s:iterator>
        
            
        
    </div>
</div>