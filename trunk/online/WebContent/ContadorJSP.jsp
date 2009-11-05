<%@page contentType="text/html"%>
<html>
<head><title>JSP Page</title></head>
<body>
 
<%
        Integer     count_o = (Integer) session.getAttribute("count");
        int         count;
        
        if(count_o == null)
            count = 0;
        else
            count = count_o.intValue();

        String comando = request.getParameter("comando");
        if(comando != null) {
         if(comando.equals("incr"))
            count++;
         else if(comando.equals("zera")) 
            count=0;
        }
                
        session.setAttribute("count",new Integer(count));

%>


<form method="post" action = "ContadorJSP.jsp">
<table bgcolor="lightGray" border="1" cellpadding='10' cellspacing='10'>
<tr>
    <td><%= count %></td>
    <td><input type="submit" name="comando" value="incr"></td>
    <td><input type="submit" name="comando" value="zera"></td>
</tr>
</table>
</form>

</body>
</html>
