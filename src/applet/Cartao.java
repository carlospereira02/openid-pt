package applet;


import java.applet.Applet;
import java.awt.Graphics;

import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;

public class Cartao extends Applet {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String nome;
	public String data;
	public String pais;
	public String sexo;
	
	
	static
	  {
	    try
	    {
	
	    	String osName = System.getProperty("os.name");
	    	if(osName.equals("Linux"))
	    		System.load("/usr/local/lib/libpteidlibj.so");
	    	else
	    		System.loadLibrary("pteidlibj");
	    	System.out.println("Operating system version =>"+ osName);
	    	
	        
	    }
	    catch (UnsatisfiedLinkError e)
	    {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	  }
	
	
	public void PrintIDData(PTEID_ID idData, Graphics g)
	  {
		nome = idData.firstname + " " + idData.name ;
		data =  idData.birthDate;
		pais = idData.country;
		sexo = idData.sex;
		
		System.out.print(nome);
	  }

		
	public void paint(Graphics g) {
	
		
		g.drawRect(0, 0, 
		getSize().width - 1,
		getSize().height - 1);
  
      
      try
      {

        pteid.Init("");

        // Don't check the integrity of the ID, address and photo (!)
        pteid.SetSODChecking(false);

        // Read ID Data
        PTEID_ID idData = pteid.GetID();
        if (null != idData)
        {
          PrintIDData(idData,g);
        }
        
        //PTEID_ADDR idData2 = pteid.GetAddr();
 
         pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
      }
  	catch (PteidException ex)
  	{
  		ex.printStackTrace();
  		//System.out.println(ex.getMessage());
  	}
  	catch (Exception ex)
  	{
  		ex.printStackTrace();
  		//System.out.println(ex.getMessage());
  	}
  	

	}
}

