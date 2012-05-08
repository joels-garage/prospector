<%@ include file="/taglibs.jsp" %>

<stripes:layout-render
  name="/layout/list.jsp"
  formActionBeanClass="PreferenceListActionBean"
  itemActionBeanClass="PreferenceActionBean"
  pageTitle="Preference List"
  itemLayout="/preference-list-item.jsp">
  
  <%-- TODO: i guess the row-rendering component would go here --%>
  
</stripes:layout-render>