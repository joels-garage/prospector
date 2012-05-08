<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the entity to display --%>
    <stripes:layout-render name="/item/model_entity.jsp" listItem="${listItem}" />
</stripes:layout-definition>