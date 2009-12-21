package applet;

import java.applet.Applet;

import pteidlib.PTEID_ADDR;
import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;


public class Test extends Applet {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	private String valError;
	
	/**
	 * init - verifica se o cartão esta activado
	 * @param args
	 */
	 public  void init()
	  {
		 
		  try
		    {					
  	    	//check reader and card
		    	pteid.Init("");			 
		        pteid.SetSODChecking(false);
		        int val = pteid.IsActivated();
		    	
		        if(val != 0){
		    		valError = "Cartão activado!";

		    	}else
		    	{
		    		valError = "Cartão não activado!";
		    		
		    	}
		    } 
		     catch(PteidException e)
		     	{
		           e.printStackTrace();
		           String msg = e.getMessage();
		           msg = msg.substring(14);
		          //inform card error
		           //setErrors(Integer.parseInt(msg));
		           
		         }  
		     
		     System.out.println(valError);
	  }
}

