<%@ include file="/taglibs.jsp" %>

<%--stripes:useActionBean beanclass="com.joelsgarage.show.action.StringPropertyActionBean" var="actionBean" /--%>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Property">
    <stripes:layout-component name="contents" >
    <p>This page describes a single property.</p>
    
    <stripes:form beanclass="com.joelsgarage.show.action.StringProperty.action" focus="">
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
     </stripes:form>
      
     <c:if test="${not empty actionBean.property}">    
      <table cellpadding=3>
        <tr>
          <td>Name:</td>
          <td>${actionBean.property.name}</td>
        </tr>
        <tr>
          <td>LastModified:</td>
          <td>${actionBean.property.lastModified}</td>
        </tr>
        <tr>
          <td>Creator:</td>
          <td>${actionBean.property.creatorKey}</td>
        </tr>
      </table>
     </c:if>
      
     <c:if test="${not empty actionBean.values}">
      <p>Extant values for this property:</p>
      <c:forEach var="val" items="${actionBean.values}">
       <ul>
        <li>${val}</li>
       </ul>
      </c:forEach>
     </c:if>
      

    </stripes:layout-component>
</stripes:layout-render>

