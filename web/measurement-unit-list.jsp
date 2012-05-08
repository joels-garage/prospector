<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="MeasurementUnitListActionBean"
  itemActionBeanClass="MeasurementUnitActionBean"
  pageTitle="Measurement Unit List"
  itemLayout="/measurement-unit-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>