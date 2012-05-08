<%@ include file="/taglibs.jsp" %>
<%-- corresponds to the abstract ModelEntityActionBean --%>

<stripes:layout-definition>
  <%-- param: pageTitle == the page title --%>
  <%-- param: formActionBeanClass == the form action --%>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="${pageTitle}">
  <stripes:layout-component name="contents" >
    <p>This page describes a single instance.</p>
    
    <stripes:form beanclass="com.joelsgarage.show.action.${formActionBeanClass}" focus="">
      <stripes:errors/>
      <table>
        <tr>
          <td>ExternalKey.namespace:</td>
          <td><stripes:text name="key.namespace"/></td>
        </tr>
        <tr>
          <td>ExternalKey.type:</td>
          <td><stripes:text name="key.type"/></td>
        </tr>
        <tr>
          <td>ExternalKey.key:</td>
          <td><stripes:text name="key.key"/></td>
        </tr>
        <tr>
          <td colspan="2">
            <stripes:submit name="fetch" value="Fetch"/>
          </td>
        </tr>
      </table>
      
      <c:if test="${not empty actionBean.instance}">
      
       <p>Attributes of ${actionBean.instance.instance.name}:</p>
  	   <table cellpadding=3>
        <c:if test="${not empty actionBean.instance.instance}">
          <tr>
            <td>Name:</td>
            <td>${actionBean.instance.instance.name}</td>
          </tr>
          <tr>
            <td>LastModified:</td>
            <td>${actionBean.instance.instance.lastModified}</td>
          </tr>
          <%-- removed instance creator field.  TODO: find it in manyToOne
          <c:if test="${not empty actionBean.instance.creator}">
            <tr>
              <td>Creator:</td>
              <td>${actionBean.instance.creator.instance.name}</td>
            </tr>
          </c:if>
          --%>
        </c:if>
       </table>
      </c:if>
      
    </stripes:form>
  </stripes:layout-component>
</stripes:layout-render>
</stripes:layout-definition>
