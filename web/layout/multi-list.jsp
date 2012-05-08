<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
  <stripes:url var="url" beanclass="${actionBean.class.name}"/>
  <%-- param: multiList, a multilist instance --%>
  
  <stripes:form beanclass="${actionBean.class.name}">
    Name Query: <stripes:text name="nameQuery"/>
    <stripes:submit name="view" value="View"/>
    <stripes:hidden name="key.type" value="${listItem.value.typeName}"/>
    <stripes:hidden name="page" value="0" /> <%-- start at the beginning --%>
    <stripes:hidden name="pageSize" value="${listItem.value.pageSize}" />
  </stripes:form>
  <c:forEach var="listItem" items="${multiList.paginatedLists}"> <%-- a map --%>
  
    <%-- listItem.value is a single paginatedList --%>
    <%--  <c:if test="${fn:length(listItem.value.list) > 1}">  --%>
    <p>List of <b>${listItem.key.name}</b></p>
    
    <p>
      <stripes:link beanclass="com.joelsgarage.show.action.CreateActionBean" event="start">
        Create new ${listItem.key.name}
        <stripes:param name="type" value="${listItem.value.typeName}"/>
        <%--stripes:param name="_sourcePage" value="${pageContext.request.servletPath}"/--%>
        <stripes:param name="_sourcePage" value="${pageContext.request.requestURL}"/>
        <stripes:param name="targetUrl" value="${url}"/>   
      </stripes:link>
    </p>
    
        <div id="listWrapper">
          <div id="resultCountWidget">       
          <div id="resultCount">
            Result count:
            <fmt:formatNumber value="${listItem.value.resultCount}" pattern="###,###,###,###"/>
          </div>
          </div>

          <c:choose>
            <c:when test="${fn:length(listItem.value.list) > 0}">
              <c:forEach var="item" items="${listItem.value.list}" varStatus="status">
                <div id="listItem">
                  <stripes:layout-render name="/item/${listItem.value.typeName}.jsp" listItem="${item}" />
                </div>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <div id="listItem">
                <i>No results</i>
              </div>
            </c:otherwise>
          </c:choose>
       
       <div id="paginationFooter">
         <c:choose>
           <c:when test="${actionBean.viewType == 'ENTITY'}">
             <%-- for an entity result, the list always says "more" and leads to a join --%>
             <stripes:link beanclass="${actionBean.class.name}">
               More
               <stripes:param name="key.type" value="${multiList.primary.type}"/>
               <stripes:param name="key.namespace" value="${multiList.primary.namespace}"/>
               <stripes:param name="key.key" value="${multiList.primary.key}" />
               <stripes:param name="joinType" value="${multiList.joinType}" />
               <stripes:param name="joinField" value="${multiList.joinField}" />
             </stripes:link>
           </c:when>
           <c:otherwise> 
             <c:choose>
               <c:when test="${listItem.value.page > 0}"> <%-- no prev for first page --%>
                 <div id="prevLink" title="Previous Page">
                   <stripes:link beanclass="${actionBean.class.name}">
                     Prev Page
                     <c:if test="${actionBean.viewType == 'JOIN'}">                       
                       <stripes:param name="key.type" value="${multiList.primary.type}"/>
                       <stripes:param name="key.namespace" value="${multiList.primary.namespace}"/>
                       <stripes:param name="key.key" value="${multiList.primary.key}" />
                       <stripes:param name="joinType" value="${multiList.joinType}" />
                       <stripes:param name="joinField" value="${multiList.joinField}" />
                     </c:if>
                     <c:if test="${actionBean.viewType == 'LIST'}">
                       <stripes:param name="key.type" value="${listItem.value.typeName}"/>
                       <stripes:param name="nameQuery" value="${multiList.nameQuery}" />
                     </c:if>
                     <stripes:param name="page" value="${listItem.value.page - 1}" />
                     <stripes:param name="pageSize" value="${listItem.value.pageSize}" />
                   </stripes:link>
                 </div>  <%-- prevLink --%>
               </c:when>
               <c:otherwise>
                 <div id="prevLink" class="disabled" title="No previous pages">
                   Prev Page
                 </div>
               </c:otherwise>
             </c:choose>
          
             <div id="pageNumber">
               page ${listItem.value.page + 1} of
               <fmt:formatNumber value="${listItem.value.totalPages}" pattern="###,###,###,###"/>
             </div>

             <c:choose>
               <c:when test="${listItem.value.more}"> <%-- no next if last page --%>
                 <div id="nextLink" title="Next Page">
                   <stripes:link beanclass="${actionBean.class.name}">
                     Next Page
                     <c:if test="${actionBean.viewType == 'JOIN'}">                       
                       <stripes:param name="key.type" value="${multiList.primary.type}"/>
                       <stripes:param name="key.namespace" value="${multiList.primary.namespace}"/>
                       <stripes:param name="key.key" value="${multiList.primary.key}" />
                       <stripes:param name="joinType" value="${multiList.joinType}" />
                       <stripes:param name="joinField" value="${multiList.joinField}" />
                     </c:if>
                     <c:if test="${actionBean.viewType == 'LIST'}">
                       <stripes:param name="key.type" value="${listItem.value.typeName}"/>
                       <stripes:param name="nameQuery" value="${multiList.nameQuery}" />
                     </c:if>
                     <stripes:param name="page" value="${listItem.value.page + 1}" />
                     <stripes:param name="pageSize" value="${listItem.value.pageSize}" />
                   </stripes:link>
                 </div>  <%-- nextLink --%>
               </c:when>
               <c:otherwise>
                 <div id="nextLink" class="disabled" title="No more pages">
                   Next Page
                 </div>
               </c:otherwise>
             </c:choose>
           </c:otherwise>
           
         </c:choose>
        </div>  <%-- paginationFooter --%>
      </div>  <%-- listWrapper --%>
      <%-- </c:if> --%> <%-- listitem length > 1 --%> 

    </c:forEach>

</stripes:layout-definition>