<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="inv" tagdir="/WEB-INF/tags" %>
<%@ page isELIgnored="false" %>

<inv:table tableName="orderitems" sortable="true" exportable="true"
dataAction="order!listItemData.bc?id=${id}" table="${listTable}" addToContainer="itemswindow" stateful="true"
customRowColors="true" customRowColorsFunction="filledRowColors"/>
