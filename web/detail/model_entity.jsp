<%@ include file="/taglibs.jsp" %>

<%-- detail for a single entity --%>

<stripes:layout-definition>
  <%-- param: singleInstance == the entity to display --%>
  
       <c:if test="${not empty singleInstance}">
       <p>Attributes of ${singleInstance.instance.name}:</p>
  	   <table cellpadding=3>
        <c:if test="${not empty singleInstance.instance}">
          <tr>
            <td>Name:</td>
            <td>${singleInstance.instance.name}</td>
          </tr>
          <tr>
            <td>LastModified:</td>
            <td>${singleInstance.instance.lastModified}</td>
          </tr>
          <%-- removed "creator" field in DisplayModelEntity.  TODO: find it in the manyToOne map.
          <c:if test="${not empty singleInstance.creator}">
            <tr>
              <td>Creator:</td>
              <td>
                <stripes:link beanclass="${actionBean.class.name}">
                  <stripes:param name="key.namespace" value="${singleInstance.creator.instance.key.namespace}"/>
                  <stripes:param name="key.type" value="${singleInstance.creator.instance.key.type}"/>
                  <stripes:param name="key.key" value="${singleInstance.creator.instance.key.key}"/>
                  ${singleInstance.creator.instance.name}
                </stripes:link>
              </td>
            </tr>
          </c:if>
          --%>
        </c:if>
        </table>
        <p>Many-to-one entities:</p>
        <table>
        <c:forEach var="dereference" items="${singleInstance.manyToOne}">
          <tr>
            <td>
              ${dereference.key}:
              <stripes:link beanclass="${actionBean.class.name}">
                <stripes:param name="key.namespace" value="${dereference.value.instance.key.namespace}"/>
                <stripes:param name="key.type" value="${dereference.value.instance.key.type}"/>
                <stripes:param name="key.key" value="${dereference.value.instance.key.key}"/>
                ${dereference.value.instance.name}
              </stripes:link>
            </td>
          </tr>
        </c:forEach>
        </table>
        <p>Fields:</p>
        <table>
        <c:forEach var="field" items="${singleInstance.fields}">
          <tr>
            <td>Field key: ${field.key}</td>
            <td>Field value: ${field.value}</td>
          </tr>
        </c:forEach>
       </table>
               <p>One-to-many entities:</p>
       
        <c:forEach var="foreign" items="${singleInstance.oneToMany}">
          <p>Entity joined by: ${foreign.key}</p>
          
          <stripes:layout-render name="/layout/multi-list.jsp" multiList="${foreign.value}"/>

        </c:forEach>

      </c:if>
        
</stripes:layout-definition>