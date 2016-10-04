<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="inv" tagdir="/WEB-INF/tags" %>
<%@ page isELIgnored="false" %>

<inv:table tableName="fastrechistory" sortable="true" exportable="true" tableTitle="Fast Rec History"
    dataAction="receivingItem!fastrecHistoryData.bc" table="${listTable}" addToContainer="fastrechistorypanel" stateful="true"/>

