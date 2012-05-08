<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="UnitSynonymListActionBean"
  itemActionBeanClass="UnitSynonymActionBean"
  pageTitle="Unit Synonym List"
  itemLayout="/unit-synonym-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>