<%@page contentType="text/html"%>
<html>
<head><title>JSP Page</title></head>
<script language="javascript">
function runTest() {
     document.getElementById("nome").value = document.getElementById("app").name;;
     document.getElementById("data").value = document.getElementById("app").numBI;;
     document.getElementById("sexo").value = document.getElementById("app").numNIF;;
     document.getElementById("pais").value = document.getElementById("app").pais;;
}
function teste(){
	document.getElementById("app").stop();	
	document.getElementById("app").destroy();	
}
function teste1(){
	document.getElementById("app").init();	
	document.getElementById("app").start();	
}
</script> 


<body >

<applet id="app" archive="bigonline.jar,pteidlibj.jar" code="applet.Simple" width="100" height="100">
</applet>

<FORM >
<input type="button" onclick="runTest()">
<input type="text" name="nome" id="nome">
<input type="text" name="data" id="data">
<input type="text" name="sexo" id="sexo">
<input type="text" name="pais" id="pais">
<input type="button" name="Reload" onclick="teste1()">
<input type="button" name="Reload" onclick="teste()">
</FORM>

 
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
