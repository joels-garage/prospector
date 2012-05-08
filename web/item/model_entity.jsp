<%@ include file="/taglibs.jsp" %>
<%@ page import="com.joelsgarage.util.NameUtil" %> 

<stripes:layout-definition>
  <%-- param: listItem == the DisplayModelEntity entity to display --%>

  <stripes:link beanclass="${actionBean.class.name}">
    <stripes:param name="key.namespace" value="${listItem.encodedNamespace}"/>
    <stripes:param name="key.type" value="${listItem.encodedType}"/>
    <stripes:param name="key.key" value="${listItem.encodedKey}"/>
    <%--
      <stripes:param name="key.namespace" value="${listItem.instance.key.namespace}"/>
      <stripes:param name="key.type" value="${listItem.instance.key.type}"/>
      <stripes:param name="key.key" value="${listItem.instance.key.key}"/>
    --%>
    item: ${listItem.instance.name}
  </stripes:link>

</stripes:layout-definition>