<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="IndividualFactListActionBean"
  itemActionBeanClass="IndividualFactActionBean"
  pageTitle="Individual Fact List"
  itemLayout="/fact-list-item.jsp">
  
  <%-- crap for this layout i need the fact and also the corresponding property, in order to get the name --%>
  
  <%-- and the unit --%>

</stripes:layout-render>