<%@ include file="../../html-start.jspf" %>
<head>
<%@ include file="../../html-head.jspf" %>

<title>Bellwether Inventory</title>

<jwr:script src="/bundles/bellInventoryBundle.js"/> 

<body style="font-size:12px;background:#fff;">

<div style="height:15px;"></div>

<table>
<tr>
<td valign="top">
<div style="font-size:14px;font-weight:bold;padding-left:15px;">Inventory Data</div>
<div style="background:#fff;padding:5px;margin:5px;border:1px solid #999;">
    <div style="padding:8px;">
        <table>
            <tr><td><div style="height:8px;"></div></td></tr>
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
            <tr>
                <td align="right" nowrap valign="top">Selling Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.sellPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Selling Percent list:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatPercent(inventory.sellPricePercentList / 100F)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <s:if test="isBcInventoryAdmin">
                <tr>
                    <td align="right" nowrap valign="top">Cost:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatMoney(inventory.cost)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
                <tr>
                    <td align="right" nowrap valign="top">Cost Percent list:</td>
                    <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="inventory.formatPercent(inventory.costPercentList / 100F)"/></td>
                </tr>
                <tr><td><div style="height:4px;"></div></td></tr>
            </s:if>
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
                <td align="right" nowrap valign="top">Last Nights Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.salesrank" default="N/A"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.author"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <%--
            <tr>
                <td align="right" nowrap valign="top">Image:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:if test="inventory.smallImage"><img src="<s:property value="inventory.smallImage"/>"/></s:if><s:else>No Image Data</s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            <tr>
                <td align="right" nowrap valign="top">Last Received Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="inventory.lastPoDate" format="MM/dd/yyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received Qty:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.lastRecQuantity" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Received PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="inventory.lastPo" default="0"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
        
        </table>
    </div>
</div>
</td>
<td valign="top">
<div style="font-size:14px;font-weight:bold;padding-left:15px;">Amazon Data</div>
<div style="background:#fff;padding:5px;margin:5px;border:1px solid #999;">
    <div style="padding:8px;">
        <s:if test="amazonData.dataLoaded">
        <table>
            <%-- 
            <tr style="padding-bottom:4px;">
                <td align="right">ASIN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.asin"/></td>
            </tr>
            --%>
            <tr>
                <td align="right" nowrap valign="top">Check Time:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="amazonData.checkTime" format="MM/dd/yyyy hh:mm a"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">ISBN:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.isbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">List Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="amazonData.listPrice"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Number Of Pages:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.numberOfPages"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sales Rank:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.salesRank"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.title"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.publisher"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Author:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.authorString"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Image:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><a href="javascript:showLargeImage('<s:if test="amazonData.largeImageUrl != null"><s:property value="amazonData.largeImageUrl"/></s:if><s:elseif test="amazonData.mediumImageUrl != null"><s:property value="amazonData.mediumImageUrl"/></s:elseif>')"><img src="<s:property value="amazonData.mediumImageUrl"/>"/></a></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publication Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="amazonData.publicationDate"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Binding:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.binding"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Length:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.length"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Width:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.width"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Height:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.height"/> inches</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Weight:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><span id="amazonWeightSpan"><s:property value="amazonData.weight"/></span> lbs (<s:property value="%{formatTwoDecimal(amazonData.weight * 16.0)}"/> ounces)</td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total New:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalNew"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest New:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestNewPriceFormatted"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Used:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalUsed"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest Used:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestUsedPriceFormatted"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Collectible:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalCollectible"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest Collectible:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestCollectiblePriceFormatted"/></td>
            </tr>
            
        </table>
        </s:if><s:elseif test="amazonData.notFound">
        This Inventory Item cannot be found by amazon.
        </s:elseif><s:else>
        There was a communication problem with amazon.
        </s:else>
    </div>
</div>
</td>
</tr>
<tr>
<td colspan="2" style="padding-top:10px;">
    <div style="font-size:14px;font-weight:bold;padding-left:15px;">Latest Amazon Orders - <s:property value="latestAmzOrders.size()"/> of <s:property value="totalAmzOrdered"/></div>
<div style="background:#fff;padding:5px;margin:5px;border:1px solid #999;">
<table>
    <tr>
    <td style="font-weight:bold">Created</td>
    <td style="padding-left:15px;font-weight:bold">Order ID</td>
    <td style="padding-left:15px;font-weight:bold">Category</td>
    <td style="padding-left:15px;font-weight:bold">Payment Status</td>
    <td style="padding-left:15px;font-weight:bold">Order Item Id</td>
    <td style="padding-left:15px;font-weight:bold">Purchase Date</td>
    <td style="padding-left:15px;font-weight:bold">Payment Date</td>
    <td style="padding-left:15px;font-weight:bold">Payment Trans Id</td>
    <td style="padding-left:15px;font-weight:bold">SKU</td>
    <td style="padding-left:15px;font-weight:bold">Listing ID</td>
    <td style="padding-left:15px;font-weight:bold">Quantity</td>
    <td style="padding-left:15px;font-weight:bold">Price</td>
</tr>
<s:iterator value="latestAmzOrders" id="lo">
<tr>
    <td><s:date name="#lo.customerOrder.createTime" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.orderId"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.category"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.paymentsStatus"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.orderItemId"/></td>
    <td style="padding-left:15px;"><s:date name="#lo.purchaseDate" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:date name="#lo.paymentsDate" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.paymentsTransactionId"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.sku"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.listingId"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.quantityPurchased"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.formatMoney(#lo.price)"/></td>
</tr>
</s:iterator>
</table>
</div>
</td>
</tr>
<tr>
<td colspan="2" style="padding-top:10px;">
    <div style="font-size:14px;font-weight:bold;padding-left:15px;">Latest Internal Orders - <s:property value="latestOrders.size()"/> of <s:property value="totalOrdered"/></div>
<div style="background:#fff;padding:5px;margin:5px;border:1px solid #999;">
<table>
<tr>
    <td style="font-weight:bold">Created</td>
    <td style="padding-left:15px;font-weight:bold">Posted</td>
    <td style="padding-left:15px;font-weight:bold">Status</td>
    <td style="padding-left:15px;font-weight:bold">PO</td>
    <td style="padding-left:15px;font-weight:bold">Invoice</td>
    <td style="padding-left:15px;font-weight:bold">Customer Code</td>
    <td style="padding-left:15px;font-weight:bold">Bin</td>
    <td style="padding-left:15px;font-weight:bold">Quantity</td>
    <td style="padding-left:15px;font-weight:bold">Shipped</td>
    <td style="padding-left:15px;font-weight:bold">Price</td>
    <td style="padding-left:15px;font-weight:bold">Salesman</td>
    <td style="padding-left:15px;font-weight:bold">Ship Date</td>
    <td style="padding-left:15px;font-weight:bold">Order Date</td>
</tr>
<s:iterator value="latestOrders" id="lo">
<tr>
    <td><s:date name="#lo.customerOrder.createTime" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.posted"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.status"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.poNumber"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.invoiceNumber"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.customerCode"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bin"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.quantity"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.filled"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.formatMoney(#lo.price)"/></td>
    <td style="padding-left:15px;"><s:property value="#lo.bellOrder.salesman"/></td>
    <td style="padding-left:15px;"><s:date name="#lo.bellOrder.shipDate" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:date name="#lo.bellOrder.orderDate" format="MM/dd/yyyy"/></td>
</tr>
</s:iterator>
</table>
</div>
</td>
</tr>
<tr>
<td colspan="2" style="padding-top:10px;">
<div style="font-size:14px;font-weight:bold;padding-left:15px;">Latest Receivings - <s:property value="latestReceived.size()"/> of <s:property value="totalReceived"/></div>
<div style="background:#fff;padding:5px;margin:5px;border:1px solid #999;">
<table>
<tr>
    <td style="font-weight:bold">Created</td>
    <td style="padding-left:15px;font-weight:bold">Date</td>
    <td style="padding-left:15px;font-weight:bold">Posted</td>
    <td style="padding-left:15px;font-weight:bold">PO</td>
    <td style="padding-left:15px;font-weight:bold">Vendor Code</td>
    <td style="padding-left:15px;font-weight:bold">Bin</td>
    <td style="padding-left:15px;font-weight:bold">Quantity</td>
    <td style="padding-left:15px;font-weight:bold">Ordered Quantity</td>
    <td style="padding-left:15px;font-weight:bold">Available</td>
    <td style="padding-left:15px;font-weight:bold">Sell Price</td>
</tr>
<s:iterator value="latestReceived" id="lr">
<tr>
    <td><s:date name="#lr.bellReceived.createTime" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:date name="#lr.bellReceived.date" format="MM/dd/yyyy"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.bellReceived.posted"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.bellReceived.poNumber"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.bellReceived.vendorCode"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.bin"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.quantity"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.orderedQuantity"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.available"/></td>
    <td style="padding-left:15px;"><s:property value="#lr.formatMoney(#lr.sellPrice)"/></td>
</tr>
</s:iterator>
</table>
</div>
</td>
</tr>
</table>

<div style="height:15px;"></div>

</body>
</html>
