<%@ include file="/taglibs.jsp" %>
<stripes:layout-definition>

  
 <br clear=all>
 <center></center>
 <br>
 <!-- blue search form and extra links at the bottom -->
 <table border=0 cellpadding=0 cellspacing=0 width=100% class="ft t bb bt">
  <tr>
   <td align=center>&nbsp;
    <br>
    <table border=0 cellpadding=0 cellspacing=0 align=center>
     <form method=GET action="/search">
      <tr>
       <td nowrap>
        <font size=-1>
         <input type=text name=q size=41 maxlength=2048 value="steel" title="Search">
         <input type=submit name="btnG" value="Search">
         <input type=hidden name=hl value="en">
         <input type=hidden name=sa value="2">
        </font>
       </td>
      </tr>
     </form>
    </table>
    <br>
    <font size=-1>
     <nobr><a href="/swr?q=steel&amp;hl=en&amp;swrnum=384000000">Search&nbsp;within&nbsp;results</a></nobr> | 
     <nobr><a href="/language_tools?q=steel&amp;hl=en">Language Tools</a></nobr> | 
     <nobr><a href="/intl/en/help.html">Search&nbsp;Tips</a></nobr> | 
     <nobr><a href="/quality_form?q=steel&amp;hl=en" target=_blank>Dissatisfied? Help us improve</a></nobr> | 
     <nobr><a href="/experimental/">Try Google Experimental</a></nobr>
    </font>
    <br>
    <br>
   </td>
  </tr>
 </table> <!-- end bottom search form -->
  </stripes:layout-definition>
 