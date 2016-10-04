<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>

<div style="background:#fff;width:100%;height:100%">
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
                <td align="left" style="padding-left:5px;" class="bluetext"><span id="amazonTitleSpan"><s:property value="amazonData.title"/></span></td>
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
                <td align="left" style="padding-left:5px;" class="bluetext"><a href="javascript:showLargeImage('<s:if test="amazonData.largeImageUrl != null"><s:property value="amazonData.largeImageUrl"/></s:if><s:elseif test="amazonData.mediumImageUrl != null"><s:property value="amazonData.mediumImageUrl"/></s:elseif>')"><img src="<s:property value="amazonData.smallImageUrl"/>"/></a></td>
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
                <td align="right" nowrap valign="top">Categories:</td>
                <td align="left" style="padding-left:5px;" class="bluetext">
                    <s:iterator value="amazonData.categories" status="status" var="cat">
                       <s:if test="#status.index > 0">, </s:if><s:property value="#cat"/>
                    </s:iterator>
                </td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Amazon Detail:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><a href="<s:property value="amazonData.detailPage"/>" target="_blank">Link</a></td>
            </tr>
            
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total New:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalNew"/></td>
            </tr>
            <tr><td><div style="height:4px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest New:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestNewPriceFormatted"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Used:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalUsed"/></td>
            </tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest Used:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestUsedPriceFormatted"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            <tr>
                <td align="right" nowrap valign="top">Total Collectible:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.totalCollectible"/></td>
            </tr>
            <tr>
                <td align="right" nowrap valign="top">Lowest Collectible:</td>
                <td align="left" style="padding-left:5px;" class="bluetext"><s:property value="amazonData.lowestCollectiblePriceFormatted"/></td>
            </tr>
            <tr><td><div style="height:10px;"></div></td></tr>
            
        </table>
        </s:if><s:elseif test="amazonData.notFound">
        This Inventory Item cannot be found by amazon.
        </s:elseif><s:else>
        There was a communication problem with amazon.<br/><br/>Click the refresh button.
        </s:else>
    </div>
</div>