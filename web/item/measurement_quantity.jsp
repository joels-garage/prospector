<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
    <%-- param: listItem == the entity to display --%>
    <%-- <stripes:layout-render name="/item/model_entity.jsp" listItem="${listItem}" /> --%>

    <stripes:link beanclass="${actionBean.class.name}">
      ${listItem.instance.name}
      <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
    </stripes:link>
        
</stripes:layout-definition>