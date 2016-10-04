<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#eaeaea;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.id"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Created:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="orderItem.createTime" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Condition:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.cond"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bin:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.quantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipped:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.filled"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Allowed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.currentAllowed"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(orderItem.inventoryItem.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(orderItem.price)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Discount:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatPercentNoMod(orderItem.discount)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Extended Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(orderItem.totalPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.vendorpo"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cover:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.inventoryItem.cover"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="orderItem.inventoryItem.publisher"/></td>
            </tr>
            
        </table>
    </div>
</div>