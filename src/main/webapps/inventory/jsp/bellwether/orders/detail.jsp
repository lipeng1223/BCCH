<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        
        <table>
            <tr>
                <td></td>
                <td>
                    <a href="order!view.bc?id=<s:property value="order.id"/>" class="actionbutton"><img src="/images/zoom.png" align="top" border="0"/> View</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Order ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.orderId"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Category:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.category"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Purchase Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="order.purchaseDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Payment Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="order.paymentsDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Payment Status:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.paymentsStatus"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Order Item ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.orderItemId"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Payment Trans Id:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.paymentsTransactionId"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">SKU:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.sku"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Listing ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.listingId"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Title:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.itemName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.quantityPurchased"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.price)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipping:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.shippingFee)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.totalPriceForDisplay)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Batch ID:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.batchId"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Buyer Email:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.buyerEmail"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Buyer Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.buyerName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Recipient Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.recipientName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Location:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.location"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship Method:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipMethod"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship Address 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipAddress1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship Address 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipAddress2"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship City:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipCity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship State:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipState"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship Zip:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipZip"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Ship Country:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.shipCountry"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Special Comments:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.specialComments"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>

            <%--
            
            <tr><td><div style="height:15px;"></div></td></tr>
            <tr>
                <td colspan="2">
                    <a href="orderinvoice.bc?id=<s:property value="order.id"/>&amp;filename=Invoice-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Invoice PDF</a> 
                    <br> 
                    <a href="orderinvoice!barcodes.bc?id=<s:property value="order.id"/>&amp;filename=Invoice-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Invoice PDF With Barcodes</a> 
                    <br> 
                    <a href="orderinvoice!notShipped.bc?id=<s:property value="order.id"/>&amp;filename=SalesOrder-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Sales Order PDF With Just Ordered Quantity</a>
                    <br> 
                    <a href="javascript:exportOrderPicklist(<s:property value="order.id"/>);"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Picklist Excel</a> 
                    <br>        
                </td>
            </tr>
            --%>
            
        </table>
    </div>
</div>