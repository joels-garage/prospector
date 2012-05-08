<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>

<stripes:useActionBean beanclass="com.joelsgarage.show.AdsActionBean" var="adBean" />

 <%-- table cellspacing=0 cellpadding=0  width=30% align=right id=mbEnd bgcolor=#ffffff border=0 class=ra --%>

 <table cellspacing=0 cellpadding=0 id=mbEnd bgcolor=#ffffff border=0 class=ra>
  <tr>
   <td colspan=4>
    <font size=-1>&nbsp;</font>
   </td>
  </tr>
  <tr>
   <td id=rhsline rowspan=5 >&nbsp;&nbsp;</td>
   <td width=1 bgcolor=#c9d7f1 rowspan=5>
    <img width=1 height=1 alt="">
   </td>
   <td rowspan=5 >&nbsp;&nbsp;</td>
   <td height=25 align=center>
    <h2 class="sl f">Sponsored Links</h2>
   </td>
  </tr>
  <tr height=7>
   <td>
    <img width=1 height=1 alt="">
   </td>
  </tr>
  <tr>
   <td class=std nowrap onmouseover="return true">

    <c:forEach var="ad" items="${adBean.ads}" varStatus="status">
     <font size=+0>
      <a id=an${status.count} href="${ad.url}">${ad.head}</a>
     </font>
     <br>
     ${ad.line1}
     <br>
     <c:if test="${not empty ad.line2}">
      ${ad.line2}
      <br>
     </c:if>
     <span class=a>${ad.advertiser}</span>
     <br>
     <c:if test="${not empty ad.location}">
      ${ad.location}
      <br>
     </c:if>
     <c:if test="${not status.last}">
      <br>
     </c:if>
    </c:forEach>
    
   </td>
  </tr>
  <tr height=7>
   <td>
    <img width=1 height=1 alt="">
   </td>
  </tr>
  <tr>
   <td height=25 align=center>
    <font size=-1></font>
   </td>
  </tr>
  <tr>
   <td id=rhspad height=0>
   </td>
  </tr>
 </table>
 
 </stripes:layout-definition>
 