<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the DisplayFact --%>
    <stripes:layout-render name="/item/model_entity.jsp" listItem="${listItem}" />
</stripes:layout-definition>