<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>

  
 <table class=tb style=clear:left width=100%>
  <tr>
   <form name=gs method=GET action="/search">
    <td class=tc valign=top>
     <a id=logo href="/home" title="Go to Google Home">Google<span></span></a>
    </td>
    <td style="padding:0 0 7px;padding-left:8px" valign=top width=100%>
     <table class=tb style="margin-top:25px">
      <tr>
       <td class=tc nowrap>
        <input type=hidden name=hl value="en">
        <input type=text name=q size=41 maxlength=2048 value="steel" title="Search">
        <input type=submit name="btnG" value="Search">
       </td>
       <td class=tc nowrap width=100%>
        <span id=ap>&nbsp;&nbsp;
         <a href="/advancedfoo">Advanced Search</a>
         <br>&nbsp;
         <a href="/preferences">Preferences</a>
        </span>
       </td>
      </tr>
     </table>
    </td>
   </tr>
  </form>
 </table>
 
  </stripes:layout-definition>
 