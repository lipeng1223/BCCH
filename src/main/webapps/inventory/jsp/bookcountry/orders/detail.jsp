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
                <td align="right" nowrap valign="top">Created:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="order.createTime" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.poNumber"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Invoice:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.invoiceNumber"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Status:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.status"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Posted:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="order.posted"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Customer Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.customerCode"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Salesman:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.salesman"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Credit:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="order.creditMemo"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr>
                <td align="right" nowrap valign="top">Debit:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="order.debitMemo"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Items:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.totalItems"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Ordered:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.totalNonShippedQuantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Shipped:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.totalQuantity"/></td>
            </tr>
            <tr><td><div style="height:15px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Extended Price:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.totalPricePreTax >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.totalPricePreTax)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Tax:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.totalTax)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipping:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.shippingCharges)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Pallete Charge:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.palleteCharge)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.totalPrice >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.totalPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Deposit:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.depositAmmount)"/></td>
            </tr>
            <tr><td><div style="height:2px;"></div></td></tr>
            <tr><td colspan="2"><div style="height:1px;background:#999;"></div></td></tr>
            <tr><td><div style="height:2px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Balance Due:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.balanceDue >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.balanceDue)"/></td>
            </tr>
            
            <tr><td><div style="height:15px;"></div></td></tr>
            
            <tr>
                <td align="right" nowrap valign="top">Customer Contact:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="order.customer.contactName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" valign="top">Customer Shipping:</td>
                <td align="left" style="padding-left:5px;" valign="top"><s:property value="order.customerShipping.viewDisplay" escape="false" /></td>
            </tr>            
            <%--
            <tr>
                <td align="right" nowrap valign="top">Total Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(order.totalExtended)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Profit / Loss:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.profitLoss >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.profitLoss)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            
            <tr><td><div style="height:15px;"></div></td></tr>
            <tr>
                <td colspan="2">
                    <a href="orderinvoice.bc?id=<s:property value="order.id"/>&amp;filename=Invoice-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Invoice PDF</a> 
                    <br> 
                    <a href="orderinvoice!barcodes.bc?id=<s:property value="order.id"/>&amp;filename=Invoice-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Invoice PDF With Barcodes</a> 
                    <br> 
                    <a href="orderinvoice!notShipped.bc?id=<s:property value="order.id"/>&amp;filename=SalesOrder-<s:property value="order.invoiceNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Sales Order PDF With Just Ordered Quantity</a>
                    <%-- 
                    <br> 
                    <a href="javascript:promptForComment(<s:property value="order.id"/>, '<s:property value="order.invoiceNumber"/>');"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Picklist PDF</a>
                    --%> 
                    <br> 
                    <a href="javascript:exportOrderPicklist(<s:property value="order.id"/>);"><img src="/images/page_white_office.png" border="0" align="bottom">&nbsp;&nbsp;Picklist Excel</a> 
                    <br>
                </td>
            </tr>
        </table>
    </div>
</div>