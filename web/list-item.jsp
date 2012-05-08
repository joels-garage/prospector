<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the DisplayFact --%>
    item: ${listItem.instance.name} (${listItem.instance.class.name})<br>
</stripes:layout-definition>