<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
    <div style="padding:8px;">
        <table>
            <tr>
                <td align="right" nowrap valign="top">Company Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.companyName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">
                    <a href="history!sales.bc?id=<s:property value="customer.id"/>" class="actionbutton"><img src="/images/date.png" align="top" border="0"/> Sales History</a>
                </td>
                <td align="left" style="padding-left:5px;" nowrap valign="top">
                    <a href="history!title.bc?id=<s:property value="customer.id"/>" class="actionbutton"><img src="/images/date.png" align="top" border="0"/> Title History</a>
                </td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Code:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.code"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Contact Name:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.contactName"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Sales Rep:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.salesRep"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Last Sales Date:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:date name="customer.lastSalesDate" format="MM/dd/yyyy"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Picklist Comment:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.picklistComment"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Email 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.email1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Email 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.email2"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Email Invoice:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.emailInvoice)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Invoice Email:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.invoiceEmail"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Comment 1:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.comment1"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Comment 2:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.comment2"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Work Phone:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.workPhone"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Cell Phone</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.cellPhone"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Home Phone:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.homePhone"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Fax:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.fax"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Address:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.addressViewDisplay" escape="false"/></td>
            </tr>
            
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Mail List:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.maillist)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Book Fair:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.bookfair)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Book Club:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.bookclub)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Tax:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.tax)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Backorder:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.backorder)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Hold:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="formatBoolean(customer.hold)" escape="false"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Terms:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.terms"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Discount:</td>
                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="customer.discount"/> %</td>
            </tr>
            
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Shipping Addresses (<s:property value="customer.customerShippings.size()" default="0"/>):</td>
                <s:if test="isBcCustomerAdmin">
                <td align="left" style="padding-left:5px;" nowrap>
                    <a href="" onclick="javascript:createShippingAddress(<s:property value="customer.id"/>);return false;" style="text-decoration:none;" title="Add New Shipping Address"><img src="/images/add.png"/></a>
                </td>
                </s:if>
            </tr>
            <tr style="padding-left:10px;">
                <td align="left" style="padding-left:5px;" nowrap colspan="2">
                    <s:iterator value="customer.customerShippingsOrdered" var="sa">
                        <s:if test="#sa.deleted == false">
                        <table style="margin-top:15px;">
                            <s:if test="isBcCustomerAdmin">
                            <tr>
                                <td nowrap>
                                    <a href="" onclick="javascript:editShippingAddress(<s:property value="#sa.id"/>);return false;" style="text-decoration:none;" title="Edit"><img src="/images/pencil.png"/></a>
                                    <a href="" onclick="javascript:deleteShippingAddress(<s:property value="#sa.id"/>);return false;" style="margin-left:8px;text-decoration:none;" title="Delete"><img src="/images/delete.png"/></a>
                                </td>
                            </tr>
                            <s:if test="!#sa.defaultShip">
                            <tr>
                                <td nowrap colspan="2">
                                    <a href="" onclick="javascript:defaultShippingAddress(<s:property value="customer.id"/>, <s:property value="#sa.id"/>);return false;" style="text-decoration:none;" title="Set As Default"><img src="/images/building_link.png"/> Set As Default</a>
                                </td>
                            </tr>
                            </s:if>
                            </s:if>
                            <tr>
                                <td align="right" nowrap valign="top">Default:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:if test="#sa.defaultShip">Yes</s:if><s:if test="!#sa.defaultShip">No</s:if></td>
                            </tr>
                            <s:if test="#sa.address1 != null && #sa.address1.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 1:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address1"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.address2 != null && #sa.address2.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 2:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address2"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.address3 != null && #sa.address3.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Address 3:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.address3"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.city != null && #sa.city.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">City:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.city"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.state != null && #sa.state.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">State:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.state"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.zip != null && #sa.zip.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Zip:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.zip"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.country != null && #sa.country.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Country:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.country"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.phone != null && #sa.phone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.phone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.workPhone != null && #sa.workPhone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Work Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.workPhone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.workExt != null && #sa.workExt.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Work Ext:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.workExt"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.homePhone != null && #sa.homePhone.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Home Phone:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.homePhone"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.fax != null && #sa.fax.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Fax:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.fax"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.email != null && #sa.email.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Email:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.email"/></td>
                            </tr>
                            </s:if>
                            <s:if test="#sa.comment != null && #sa.comment.length() > 0">
                            <tr>
                                <td align="right" nowrap valign="top">Comment:</td>
                                <td align="left" style="padding-left:5px;" class="bluetext" nowrap><s:property value="#sa.comment"/></td>
                            </tr>
                            </s:if>
                        </table>
                        </s:if>
                    </s:iterator>
                </td>
            </tr>
        
        </table>
    </div>
</div>