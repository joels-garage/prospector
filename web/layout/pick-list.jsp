<%@ include file="/taglibs.jsp" %>

<stripes:layout-definition>
  <stripes:url var="url" beanclass="${actionBean.class.name}"/>
  <%-- param: multiList, a multilist instance --%>
  
  This is page ${actionBean.position + 1} of ${actionBean.maxPosition + 1}
  
  <stripes:form beanclass="${actionBean.class.name}">
    Name Query: <stripes:text name="nameQuery"/>
    <stripes:submit name="update" value="View"/>
    <stripes:hidden name="key.type" value="${listItem.value.typeName}"/>
    <stripes:hidden name="page" value="0" /> <%-- start at the beginning --%>
    <stripes:hidden name="pageSize" value="${listItem.value.pageSize}" />
  </stripes:form>
  <c:forEach var="listItem" items="${multiList.paginatedLists}"> <%-- a map --%>
    <stripes:form beanclass="${actionBean.class.name}">
  
    <%-- listItem.value is a single paginatedList --%>
    <%--  <c:if test="${fn:length(listItem.value.list) > 1}">  --%>
    <p>List of <b>${listItem.key.name}</b></p>
    
        <div id="listWrapper">
          <div id="resultCountWidget">       
          <div id="resultCount">
            Result count:
            <fmt:formatNumber value="${listItem.value.resultCount}" pattern="###,###,###,###"/>
          </div>
          </div>
          
          
          <br>
          some debugging:
          <br>
          field name ${fieldName}
          <br>
          key ${manyToOne[fieldName]}
          <br>
   
          <br>
          
          
          <c:choose>
            <c:when test="${fn:length(listItem.value.list) > 0}">
              <c:forEach var="item" items="${listItem.value.list}" varStatus="status">
                <div id="listItem">
                  <stripes:layout-render name="/item/${listItem.value.typeName}.jsp" listItem="${item}" />
                    <stripes:radio
                      name="choice[${actionBean.position}]"
                      value="${status.index}" />
                    key: ${item.instance.key}
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
               <c:when test="${listItem.value.page > 0}"> <%-- no prev for first page --%>
                 <div id="prevLink" title="Previous Page">
                   <stripes:link beanclass="${actionBean.class.name}" event="update">
                     Prev Page
                     <stripes:param name="key.type" value="${listItem.value.typeName}"/>
                     <stripes:param name="nameQuery" value="${multiList.nameQuery}" />
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
                   <stripes:link beanclass="${actionBean.class.name}"  event="update">
                     Next Page
                     <stripes:param name="key.type" value="${listItem.value.typeName}"/>
                     <stripes:param name="nameQuery" value="${multiList.nameQuery}" />
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
             
        </div>  <%-- paginationFooter --%>
      </div>  <%-- listWrapper --%>
      <%-- </c:if> --%> <%-- listitem length > 1 --%> 
      
      <stripes:hidden name="typeName[${actionBean.position}]" value="${listItem.value.typeName}"/>
        <c:choose>
          <c:when test="${actionBean.position < actionBean.maxPosition}">
            <stripes:submit name="choose" value="Next"/>
          </c:when>
          <c:otherwise>
            <stripes:submit name="chooseAndFinish" value="Finish"/>
          </c:otherwise>
        </c:choose>
      </stripes:form>
      
      
    </c:forEach>

</stripes:layout-definition>