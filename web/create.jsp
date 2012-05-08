<%@ include file="/taglibs.jsp" %>

<stripes:layout-render name="/layout/default.jsp"  pageTitle="Joel's Garage">
  <stripes:layout-component name="contents" >
  
    Create a new instance of type ${actionBean.type}.  This is page ${actionBean.position + 1} of ${actionBean.maxPosition + 1}.
    <br>
    Fields:
    <c:forEach var="item" items="${actionBean.fieldPosition}">
      ${item.key} : 
      <c:forEach var="listItem" items="${item.value}">
        ${listItem}
      </c:forEach>
    </c:forEach>
    <br>
    Keys
    <br>
    <c:forEach var="item" items="${actionBean.keyPosition}">
      ${item.key} : ${item.value}
    </c:forEach>
    
    <c:choose>
    <c:when test="${not empty actionBean.keyPosition[actionBean.position]}">
      <p>
        Picklist
      </p>
      <stripes:layout-render name="/layout/pick-list.jsp" multiList="${actionBean.multiList}"/>
    </c:when>
    
    <c:when test="${not empty actionBean.fieldPosition[actionBean.position]}">
      <%-- it's a form --%>
      <stripes:form beanclass="${actionBean.class.name}">
        <%-- in general there is a list of form elements --%>
        <c:forEach var="item" items="${actionBean.fieldPosition[actionBean.position]}">
          ${item}: <stripes:text name="fields[${item}]"/>
          <br>
        </c:forEach>

        <c:choose>
          <c:when test="${actionBean.position < actionBean.maxPosition}">
            <stripes:submit name="more" value="Next"/>
          </c:when>
          <c:otherwise>
            <stripes:submit name="finish" value="Finish"/>
          </c:otherwise>
        </c:choose>
      </stripes:form>
    </c:when>
    
    <c:otherwise>
      Something really strange happened.
    </c:otherwise>
    
    </c:choose>
    
  </stripes:layout-component>
</stripes:layout-render>

