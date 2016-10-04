<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <tr>
                <td></td>
                <td>
                    <a href="receiving!view.bc?id=<s:property value="receiving.id"/>" class="actionbutton"><img src="/images/zoom.png" align="top" border="0"/> View</a>
                </td>
            </tr>
            <tr><td><div style="height:8px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Created:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="receiving.createTime" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Posted:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="receiving.posted"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Post Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="receiving.postDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">PO:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.poNumber"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">PO Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="receiving.poDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">PO Total:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatMoney(receiving.poTotal)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Items:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.totalItems"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.totalQuantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Ordered Quantity:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.totalOrderedQuantity"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Extended Cost:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receiving.totalExtendedCost)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <%--
            <tr>
                <td align="right" nowrap valign="top">Total Sell Price:</td>
                <td align="left" style="padding-left:5px;" class="greentext" nowrap><s:property value="formatMoney(receiving.totalSellPrice)"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Publisher Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.publisherCode"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Trans No:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.transno"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Clerk:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.clerk"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Due Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="receiving.dueDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Trans No:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.transno"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="receiving.skid"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Break:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="receiving.skidBreak"><span class='greentext'>Yes</span></s:if><s:else><span class='redtext'>No</span></s:else></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Skid Isbn:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.skidIsbn"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Comment:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.comment"/></td>
            </tr>
            
            
            <tr><td><div style="height:15px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendorCode"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendor.vendorName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Account Number:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendor.accountNumber"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Shipping Company:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendor.shippingCompany"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Terms:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendor.terms"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Vendor Shipping:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="receiving.vendor.shippingViewDisplay"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <%--
            <tr>
                <td colspan="2" nowrap valign="top">
                <a href="receivingreport!report.bc?id=<s:property value="receiving.id"/>&amp;filename=Receiving-<s:property value="receiving.poNumber"/>.pdf"><img src="/images/page_white_acrobat.png" border="0" align="bottom">&nbsp;&nbsp;Receiving Report PDF</a>
                </td>
            </tr> 
            <tr><td><div style="height:4px;"></div></td></tr>
            --%>
            
        </table>
    </div>
</div>