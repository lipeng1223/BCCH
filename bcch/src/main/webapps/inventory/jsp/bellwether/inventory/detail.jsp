<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <%--
            <tr>
                <td></td>
                <td>
                    <a href="inventory!view.bc?id=<s:property value="inventory.id"/>" class="actionbutton"><img src="/images/zoom.png" align="top" border="0"/> View</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            --%>
            <%--
            <tr>
                <td></td>
                <td>
                    <a href="javascript:showInventoryHistory(<s:property value="inventory.id"/>)" class="actionbutton"><img src="/images/calendar.png" align="top" border="0"/> History</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            --%>
            <tr>
                <td align="right" nowrap valign="top">Bin:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 10:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.isbn10"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cover:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.cover"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <%--
            <tr>
                <td align="right" nowrap valign="top">Selling Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.sellingPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            <tr>
                <td align="right" nowrap valign="top">Low Used Price:</td>
                <td align="left" style="padding-left:5px;" nowrap>
                    <span class="greentext"><s:property value="inventory.formatMoney(inventory.lowUsed)"/></span>
                </td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top"></td>
                <td align="left" style="padding-left:5px;" nowrap>
                    <span class="bluetext"><s:date name="inventory.lastAmzCheck" format="MM/dd/yyy hh:mm a"/></span>
                </td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.salesrank"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top"></td>
                <td align="left" style="padding-left:5px;" nowrap>
                    <span class="bluetext"><s:date name="inventory.lastAmzCheck" format="MM/dd/yyy hh:mm a"/></span>
                </td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">On Hand:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.onhand" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Available:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.available" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Committed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.committed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Listed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventory.listed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last List Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventory.lastListDate" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
        </table>
            
            <s:if test="inventory.bellSkus != null && inventory.bellSkus.size() > 0">
        <table>
            <tr><td><div style="height:15px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top" colspan="2">
                    <table>
                    <tr>
                        <td align="center">SKU</td>
                        <td align="center" style="padding-left:10px;">Listed</td>
                        <td align="center" style="padding-left:10px;">Selling Price</td>
                        <td align="center" style="padding-left:10px;">Condition</td>
                        <td align="center" style="padding-left:10px;">Location</td>
                        <td align="center" style="padding-left:10px;">Lowest?</td>
                    </tr>
                    <s:iterator value="inventory.bellSkus" status="status" id="sku">
                        <tr>
                        <td class="bluetext"><s:property value="#sku.sku"/></td>
                        <td style="padding-left:10px;" class="bluetext"><s:property value="#sku.listed"/></td>
                        <td style="padding-left:10px;" class="greentext"><s:property value="#sku.formatMoney(#sku.sellPrice)"/></td>
                        <td style="padding-left:10px;" class="bluetext"><s:property value="#sku.conditionString"/></td>
                        <td style="padding-left:10px;" class="bluetext"><s:property value="#sku.location"/></td>
                        <td style="padding-left:10px;" class="bluetext"><s:property value="#sku.isLowest" escape="false"/></td>
                        </tr>
                    </s:iterator>
                    </table>
                </td>
            </tr>
            <tr><td><div style="height:15px;"></div></td></tr>
        </table>
            </s:if>
            
        <table>
            <s:if test="isBellInventoryAdmin">
                <tr>
                    <td align="right" nowrap valign="top">Cost:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.cost)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </s:if>
            <tr>
                <td align="right" nowrap valign="top">Received Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.receivedPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Received Discount:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.receivedDiscount"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.companyRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.author"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.category"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bell Comment:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.bellcomment"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            
        </table>
    </div>
</div>