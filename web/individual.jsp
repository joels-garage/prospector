<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Individual">
  <stripes:layout-component name="contents" >
    <p>This page describes a single individual.</p>
    
    <stripes:form beanclass="com.joelsgarage.show.action.IndividualActionBean" focus="">
      <stripes:errors/>
      <table>
      <%--
      
      Here we want to embed three little fact lists, one for each fact subclass.
      
      So, three little lists.
      
      <stripes:layout-render name="individual facts" listItem="some sort of constraint" />
      
      <stripes:layout-render name="quantity facts" listItem="some sort of constraint" />
      
      <stripes:layout-render name="string facts" listItem="some sort of constraint" />
      
      same constraint in each case of course.
      
      also, don't use regular pagination -- use a flag that says "just link to "more" page".
      
      
      also this zillion-jsp method is ridiculously tedious.
      
      instead just configure a map of subclasses.
      
      
      
      Don't yet have the fact list working.
        <tr>
          <td>PageSize:</td>
          <td><stripes:text name="pageSize"/></td>
        </tr>
        <tr>
          <td>Page:</td>
          <td><stripes:text name="page"/></td>
        </tr>
        --%>
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
      
      <%--
      <c:if test="${not empty actionBean.instance && not empty actionBean.facts}">
      --%>
       <p>Attributes of ${actionBean.instance.instance.name}:</p>
  	   <table cellpadding=3>
        <c:if test="${not empty actionBean.instance}">
        <tr>
          <td>Name:</td>
          <td>${actionBean.instance.instance.name}</td>
        </tr>
        <tr>
          <td>LastModified:</td>
          <td>${actionBean.instance.instance.lastModified}</td>
        </tr>
        <tr>
          <td>Creator:</td>
          <td>${actionBean.instance.instance.creatorKey}</td>
        </tr>
        </c:if>
        
        <%--
        TODO: figure out how to embed the other fact-list jsps here
        
        <c:forEach var="index" begin="0" end="${fn:length(actionBean.facts)}">
         <tr>
          <td>
           <b>
            Fact class name: ${actionBean.facts[index].class.name}<br>
            <c:choose>
             <c:when test="${actionBean.facts[index].class.name == 'com.joelsgarage.prospector.client.model.IndividualFact'}">
               Individual:
              <stripes:link beanclass="com.joelsgarage.show.action.IndividualPropertyActionBean">
               ${actionBean.properties[index].name}:
               <stripes:param name="key.namespace" value="${actionBean.properties[index].key.namespace}"/>
               <stripes:param name="key.type" value="${actionBean.properties[index].key.type}"/>
               <stripes:param name="key.key" value="${actionBean.properties[index].key.key}"/>
              </stripes:link>
              TODO: look up the value
             </c:when>
             <c:when test="${actionBean.facts[index].class.name == 'com.joelsgarage.prospector.client.model.MeasurementFact'}">
              Measurement:
              <stripes:link beanclass="com.joelsgarage.show.action.MeasurementPropertyActionBean">
               ${actionBean.properties[index].name}:
               <stripes:param name="key.namespace" value="${actionBean.properties[index].key.namespace}"/>
               <stripes:param name="key.type" value="${actionBean.properties[index].key.type}"/>
               <stripes:param name="key.key" value="${actionBean.properties[index].key.key}"/>
              </stripes:link>
             </c:when>
             <c:when test="${actionBean.facts[index].class.name == 'com.joelsgarage.prospector.client.model.QuantityFact'}">
              Quantity:
              <stripes:link beanclass="com.joelsgarage.show.action.QuantityPropertyActionBean">
               ${actionBean.properties[index].name}:
               <stripes:param name="key.namespace" value="${actionBean.properties[index].key.namespace}"/>
               <stripes:param name="key.type" value="${actionBean.properties[index].key.type}"/>
               <stripes:param name="key.key" value="${actionBean.properties[index].key.key}"/>
              </stripes:link>
             </c:when>
             <c:when test="${actionBean.facts[index].class.name == 'com.joelsgarage.prospector.client.model.StringFact'}">
              String:
              <stripes:link beanclass="com.joelsgarage.show.action.StringPropertyActionBean">
               ${actionBean.properties[index].name}:
               <stripes:param name="key.namespace" value="${actionBean.properties[index].key.namespace}"/>
               <stripes:param name="key.type" value="${actionBean.properties[index].key.type}"/>
               <stripes:param name="key.key" value="${actionBean.properties[index].key.key}"/>
              </stripes:link>
             </c:when>
            </c:choose>
           </b>
          </td>
          <td>
            ${actionBean.facts[index].value}

            <c:if test="${not empty actionBean.units[index]}">
              (
                <stripes:link beanclass="com.joelsgarage.show.action.UnitSynonymActionBean">
                  ${actionBean.units[index].value}
                  <stripes:param name="key.namespace" value="${actionBean.units[index].key.namespace}"/>
                  <stripes:param name="key.type" value="${actionBean.units[index].key.type}"/>
                  <stripes:param name="key.key" value="${actionBean.units[index].key.key}"/>
                </stripes:link>
              )
            </c:if>
          </td>
         </tr>
        </c:forEach>  
        --%>
       </table>
       <%--
      </c:if>
      --%>
    </stripes:form>
  </stripes:layout-component>
</stripes:layout-render>

