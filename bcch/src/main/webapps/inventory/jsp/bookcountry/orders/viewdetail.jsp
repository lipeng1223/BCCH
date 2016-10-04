<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div id="orderDetail" style="margin:8px;margin-left:25px;">

<table>
    <tr>
        <td align="left" style="padding-left:20px;" valign="top">
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
            <div style="height:15px"></div>
            <table>
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
                <td align="left" style="padding-left:5px;" class="greentext" nowrap id="tdShippingCharges"><s:property value="formatMoney(order.shippingCharges)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Pallete Charge:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap id="tdPalleteCharge"><s:property value="formatMoney(order.palleteCharge)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.totalPrice >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.totalPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Deposit:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap id="tdDepositeAmount"><s:property value="formatMoney(order.depositAmmount)"/></td>
            </tr>
            <tr><td><div style="height:2px;"></div></td></tr>
            <tr><td colspan="2"><div style="height:1px;background:#999;"></div></td></tr>
            <tr><td><div style="height:2px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Balance Due:</td>
                <td align="left" style="padding-left:5px;" class="<s:if test="order.balanceDue >= 0">greentext</s:if><s:else>redtext</s:else>" nowrap><s:property value="formatMoney(order.balanceDue)"/></td>
            </tr>
            </table>
        </td>
        
        <td style="padding-left:20px;" valign="top">
            <table>
                <tr>
                    <td align="right" class="tdName" >Credit Memo:</td>
                    <td align="left" style="padding-left:8px;"><s:if test="order.creditMemo"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Credit Memo Type:</td>
                    <td align="left" style="padding-left:8px;"><s:if test="order.creditMemo"><s:property value="order.creditMemoTypeDisplay"/></s:if></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName" >Posted:</td>
                    <td align="left" style="padding-left:8px;"><s:if test="order.posted"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Post Date:</td>
                    <td align="left" style="padding-left:8px;"><s:date name="order.postDate" format="MM/dd/yyyy" /></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Invoice Number:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.invoiceNumber"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Customer:</td>
                    <td align="left" style="padding-left:8px;" id="tdCustomer"><s:property value="order.customer.companyName"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Status:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.status"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;" id=tdCustomerCode">Customer Code:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.customer.code"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Sales Rep:</td>
                    <td align="left" style="padding-left:8px;" id="tdSalesMan"><s:property value="order.salesman"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Customer Contact:</td>
                    <td align="left" style="padding-left:8px;" id="customerContact"><s:property value="order.customer.contactName"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Order Date:</td>
                    <td align="left" style="padding-left:8px;" id="tdOrderDate"><s:date name="order.orderDate" format="MM/dd/yyyy"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Customer PO Number:</td>
                    <td align="left" style="padding-left:8px;" id="tdPoNumber"><s:property value="order.poNumber"/></td>
                </tr>
                <tr><td style="height:15px;"></td></tr>
                
                <tr>
                    <td align="right" class="tdName">Total Items:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.totalItems"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;" valign="top">Customer Shipping:</td>
                    <td align="left" style="padding-left:8px;" rowspan="6" valign="top" id="tdCustomerShipping"><s:property value="order.customerShipping.viewDisplay" escape="false" /></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Total Ordered:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.totalNonShippedQuantity"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Total Shipped:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.totalQuantity"/></td>
                </tr>
                
            
                <tr><td style="height:15px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Posted By:</td>
                    <td align="left" style="padding-left:8px;"><s:property value="order.postedBy"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Ship Date:</td>
                    <td align="left" style="padding-left:8px;"><s:date name="order.shipDate" format="MM/dd/yyyy"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName" valign="top">Customer Visit:</td>
                    <td align="left" style="padding-left:8px;" valign="top" id="tdCustomerVisit"><s:if test="order.customerVisit"><span style="greentext">Yes</span></s:if><s:else><span style="redtext">No</span></s:else></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName" valign="top">Quality Control:</td>
                    <td align="left" style="padding-left:8px;" id="tdQualityControl"><s:property value="order.qualityControl"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName" valign="top">Ship Via:</td>
                    <td align="left" style="padding-left:8px;" valign="top" id="tdShipVia"><s:property value="order.shipVia" escape="false" /></td>
                </tr>
                
                <tr><td style="height:10px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Picker 1:</td>
                    <td align="left" style="padding-left:8px;" id="tdPicker1"><s:property value="order.picker1"/></td>
                    
                    <td align="right" class="tdName" style="padding-left:50px;">Picker 2:</td>
                    <td align="left" style="padding-left:8px;" id="tdPicker2"><s:property value="order.picker2"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName" id="tdComment">Comment:</td>
                    <td align="left" style="padding-left:8px;" colspan="3"><s:property value="order.comment"/></td>
                </tr>
                <tr><td style="height:4px;"></td></tr>
                <tr>
                    <td align="right" class="tdName">Comment 2:</td>
                    <td align="left" style="padding-left:8px;" colspan="3" id="tdComment2"><s:property value="order.comment2"/></td>
                </tr>
            
            </table>
        </td>
    
    </tr>
    
</table>

 
</div>
