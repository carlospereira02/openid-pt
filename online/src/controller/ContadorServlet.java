package controller;

import javax.servlet.*;
 import javax.servlet.http.*;

 public class ContadorServlet extends HttpServlet {
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void destroy() { }
    
    protected void processRequest(HttpServletRequest request, 
				  HttpServletResponse response)
	throws ServletException, java.io.IOException {
	
	HttpSession session = request.getSession();
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
	
	show(response,count);
    }

    private void show(HttpServletResponse response, int count)
    throws ServletException, java.io.IOException {
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");  
        out.println("</head>");
        out.println("<body>");

        out.println("<form method='post' action='ContadorServlet'>");
        out.print("<table bgcolor='lightGray' border='1' ");
	out.println("cellpadding='10' cellspacing='10'>");
        out.println("<tr>");
        out.println("<td>"+count+"</td>");
        out.print("<td><input type='submit' ");
	out.println("name='comando' value='incr'></td>");
        out.print("<td><input type='submit' ");
	out.println("name='comando' value='zera'></td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</form>");
         
        out.println("</body>");
        out.println("</html>");
       
        out.close();
    } 

    protected void doGet(HttpServletRequest request, 
			 HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);
    } 

    protected void doPost(HttpServletRequest request, 
			  HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);     
    }

    public String getServletInfo() { return "Contador"; }

 }
