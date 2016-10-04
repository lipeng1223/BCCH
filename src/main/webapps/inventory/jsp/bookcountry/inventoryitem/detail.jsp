<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <tr>
                <td></td>
                <td>
                    <a href="inventoryitem!view.bc?id=<s:property value="inventoryItem.id"/>" class="actionbutton"><img src="/images/zoom.png" align="top" border="0"/> View</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td></td>
                <td>
                    <a href="javascript:showInventoryHistory(<s:property value="inventoryItem.id"/>)" class="actionbutton"><img src="/images/calendar.png" align="top" border="0"/> History</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td></td>
                <td>
                    <a href="javascript:showPrintInv(<s:property value="inventoryItem.id"/>)" class="actionbutton"><img src="/images/printer.png" align="top" border="0"/> Print</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bin:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.bin"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Condition:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.cond"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 10:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn10"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN 13:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.isbn13"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cover:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.cover"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bell Book:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.bellbook">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.bellbook">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Restricted:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.restricted">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.restricted">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">HE:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.he">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.he">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Back Stock:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="inventoryItem.backStock">greentext</s:if><s:else>redtext</s:else>" nowrap><s:if test="inventoryItem.backStock">Yes</s:if><s:else>No</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.listPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.sellingPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Percent list:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatPercent(inventoryItem.sellPricePercentList / 100F)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <s:if test="isBcInventoryAdmin">
                <tr>
                    <td align="right" nowrap valign="top">Cost:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatMoney(inventoryItem.cost)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Cost Percent list:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventoryItem.formatPercent(inventoryItem.costPercentList / 100F)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </s:if>
            <tr>
                <td align="right" nowrap valign="top">On Hand:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.onhand" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Available:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.available" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Committed:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="inventoryItem.committed" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Number Of Pages:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.numberOfPages"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.salesRank" default="N/A"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Amazon Update:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:date name="inventoryItem.lastAmazonUpdate" format="MM/dd/yyyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Company Rec:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.companyRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Imprint Rec:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.imprintRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.companyRec"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.author"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <%--
            <tr>
                <td align="right" nowrap valign="top">Image:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:if test="inventoryItem.smallImage"><img src="<s:property value="inventoryItem.smallImage"/>"/></s:if><s:else>No Image Data</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            <tr>
                <td align="right" nowrap valign="top">Last Received Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventoryItem.lastpoDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.receivedQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Pre-receiving Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="prereceivingQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.lastpo" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publication Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventoryItem.publishDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Length:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.length" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Width:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.width" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Height:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.height" default="N/A"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Weight:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.weight" default="N/A"/> lbs (<s:property value="inventoryItem.weight * 16.0"/> ounces)</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Bc Category:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.bccategory"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category2"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 3:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category3"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category 4:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventoryItem.category4"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
        
        </table>
    </div>
</div>