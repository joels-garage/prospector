<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the entity to display --%>
    <p>
    Model Entity stuff:
    </p>
    <stripes:layout-render name="/detail/model_entity.jsp" singleInstance="${singleInstance}" />
    
    TODO: display facts here.
    
</stripes:layout-definition>