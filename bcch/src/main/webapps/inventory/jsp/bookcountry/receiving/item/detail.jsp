<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#eaeaea;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
        
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.id"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Created:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="receivedItem.createTime" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Condition:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.cond"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bin:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.quantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ordered Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.orderedQuantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Available:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.available"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sell Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.sellPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.cost)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Extended Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.extendedCost)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Discount:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatPercent(receivedItem.discount)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Type:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.type"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Break Room:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(receivedItem.breakRoom)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Book Type:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.bookType"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cover Type:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.coverType"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lbs:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.lbs"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lbs Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.lbsPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lbs Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.lbsCost)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Pieces:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.pieces"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(receivedItem.skid)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Piece Count:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.skidPieceCount"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Piece Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.skidPiecePrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Piece Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receivedItem.skidPieceCost)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Percentage List:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receivedItem.percentageList"/></td>
            </tr>
            
        </table>
    </div>
</div>