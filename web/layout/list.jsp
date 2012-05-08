<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/taglibs.jsp" %>
<%-- Corresponds to the abstract ListActionBean --%>


<stripes:layout-definition>
  <%-- param: formActionBeanClass == the form action (i.e. this page), within com.joelsgarage.show.action. --%>
  <%-- param: itemActionBeanClass == the item-detail page action, within com.joelsgarage.show.action. --%>
  <%-- param: pageTitle == the page title --%>
  <%-- param: itemLayout == the JSP to use for each item --%>
   
  <stripes:layout-render name="/layout/default.jsp" pageTitle="${pageTitle}">
  
    <stripes:layout-component name="contents" >
      <p>This page describes a list.</p>
       
      <stripes:form beanclass="com.joelsgarage.show.action.${formActionBeanClass}" focus="">
    
        <stripes:errors/>
        <table>
          <tr>
            <td>PageSize:</td>
            <td><stripes:text name="pageSize"/></td>
          </tr>
          <tr>
            <td>Page:</td>
            <td><stripes:text name="page"/></td>
          </tr>
          <tr>
            <td colspan="2">
              <stripes:submit name="fetch" value="Fetch"/>
            </td>
          </tr>
        </table>
      </stripes:form>
    


      <c:if test="${not empty actionBean.list}">
        <p>The list:</p>
        <div id="listWrapper">
        
          <div id="resultCountWidget">       
          <div id="resultCount">
            Result count:
            <fmt:formatNumber value="${actionBean.resultCount}" pattern="###,###,###,###"/>
          </div>
          </div>
          
        
        <!-- class name: ${actionBean.list[0].class.name} -->
       
          <c:forEach var="item" items="${actionBean.list}">
            <div id="listItem">
                <stripes:layout-render name="${itemLayout}" listItem="${item}" />
                <%--
                <stripes:layout-render name="/fact-list-item.jsp" listItem="${item}" />
                --%>
                
                <%--
                <stripes:layout-component name="list-item" listItem="${item}">
                This is the superclass.
                --%>
                  <%-- This is just a list using ModelEntity members.
                       TODO: do a table with some sort of row renderer template --%>
                <%--
                  <stripes:link beanclass="com.joelsgarage.show.action.${itemActionBeanClass}" event="fetch">
                    ${item.name}&nbsp;:&nbsp;
                    <stripes:param name="key.namespace" value="${item.key.namespace}"/>
                    <stripes:param name="key.type" value="${item.key.type}"/>
                    <stripes:param name="key.key" value="${item.key.key}"/>
                  </stripes:link>
                </stripes:layout-component>
                --%>
            </div> <%-- listItem --%>
          </c:forEach>  
          
          <div id="paginationFooter">
             
          <c:if test="${actionBean.page > 0}"> <%-- no prev for first page --%>
            <div id="prevLink">
              <stripes:link beanclass="com.joelsgarage.show.action.${formActionBeanClass}">
                Prev Page
                <stripes:param name="page" value="${actionBean.page - 1}" />
                <stripes:param name="pageSize" value="${actionBean.pageSize}" />
                <%-- TODO: add more context passthru --%>
              </stripes:link>
            </div>  <%-- prevLink --%>
          </c:if>
          
          <div id="pageNumber">
            page ${actionBean.page + 1} of
            <fmt:formatNumber value="${actionBean.totalPages}" pattern="###,###,###,###"/>
          </div>

          <c:if test="${actionBean.more}"> <%-- no next if last page --%>
            <div id="nextLink">
              <stripes:link beanclass="com.joelsgarage.show.action.${formActionBeanClass}">
                Next Page
                <stripes:param name="page" value="${actionBean.page + 1}" />
                <stripes:param name="pageSize" value="${actionBean.pageSize}" />    
                <%-- TODO: add more context passthru --%>    
              </stripes:link>
            </div>  <%-- nextLink --%>
          </c:if>

        </div>  <%-- paginationFooter --%>
        </div>  <%-- listWrapper --%>
      </c:if>  

    </stripes:layout-component>
  </stripes:layout-render>
</stripes:layout-definition>