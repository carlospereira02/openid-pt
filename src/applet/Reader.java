package applet;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pteidlib.PTEID_ADDR;
import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;

public class Reader {
		
	public String name = null;
	public String numNIF = null;
	public String numBI = null;
	public String birthDate = null;
	public String certserial = null;
	public int errors = 0;
	public String valError = "";
	public String cardType = null;
	public String serialCertBI = null;
	public String   street= null;
	public String   streetType= null;
	public String   cp4= null;
	public String   cp3= null;
	public String   municipality= null;
		static
		  {
		    try
		    {
		      System.loadLibrary("pteidlibj");
		    }
		    catch (UnsatisfiedLinkError e)
		    {
		      System.err.println("Native code library failed to load.\n" + e);
		      System.exit(1);
		    }
		  }

		  public void PrintIDData(PTEID_ID idData)
		  {
		   name= idData.firstname+" "+idData.name;
		   numNIF= idData.numNIF;
		   numBI= idData.numBI;
		   //System.out.println("TESTE="+numBI);
		   birthDate= idData.birthDate;
		   
		  }
		  public void PrintADData(PTEID_ADDR adData)
		  {
		   street= adData.street;
		   cp4= adData.cp4;
		   cp3= adData.cp3;
		   municipality= adData.municipalityDesc;
		   streetType = adData.streettype;
		  }
		  public void GetcardType(int card){
			  switch (card)
		   	  	{
		   	  	case pteid.CARD_TYPE_IAS07:
		   	  	cardType=("IAS 0.7 card");
		   	  		break;
		   		case pteid.CARD_TYPE_IAS101:
		   			cardType=("IAS 1.0.1 card");
		   			break;
		   		case pteid.CARD_TYPE_ERR:
		   			cardType=("Unable to get the card type");
		   			break;
		   		default:
		   			cardType=("Unknown card type");
		   	  }
			  
		  }
		  
		  public void setErrors(int error){
			  errors = error;			  
		  }
		  public void printdata(){
			  System.out.println(name );
			  System.out.println(numNIF); 
			  System.out.println( numBI );
			  System.out.println( birthDate);
			  System.out.println( certserial); 
			  System.out.println( errors );
			  System.out.println( cardType );
			  System.out.println( serialCertBI);
			  System.out.println( streetType+" "+street);
			  System.out.println( cp4+"-"+cp3+" "+municipality);
			//  System.out.println( );
			 // System.out.println( ); 
			  
		  }
		  public static void main(String[] args)
		  {
			  Reader card = new Reader();
			    try
			    {					
	    	    	//check reader and card
			    	pteid.Init("");			 
			        pteid.SetSODChecking(false);
			        int val = pteid.IsActivated();
			    	
			        if(val != 0){
				        //read card type
				    	int cardtype = pteid.GetCardType();	
				    	card.GetcardType( cardtype);
					   
				    	// Read ID Data
				    	PTEID_ID idData = pteid.GetID();
						 if (null != idData)
						    {
							card.PrintIDData(idData);
						    }
						 
						// Read ADDRESS Data
					    	PTEID_ADDR adData = pteid.GetAddr();
							 if (null != adData)
							    {
								card.PrintADData(adData);
							    }
						 
						 //validate cert data
					     card.getCert(card);
					     card.verifyCert(card.numBI);
					     card.printdata();
			    	}else
			    	{
			    		card.valError = "Cartão não activado!";
			    		
			    	}
			    } 
			     catch(PteidException e)
			     	{
			           e.printStackTrace();
			           String msg = e.getMessage();
			           msg = msg.substring(14);
			          //inform card error
			           card.setErrors(Integer.parseInt(msg));
			           
			         }     
			     System.out.println(card.valError);
		  } 
		
		  private void verifyCert(String numBI){
			  if (serialCertBI.equals("BI"+numBI)){
				  valError=valError+"BI E CERTIFICADO CONFEREM!!";				  
			  }
			  else{				  
				  valError=valError+"BI E CERTIFICADO DIFEREM!! numBI="+numBI+"; serialBI="+serialCertBI;
			  }
			  
		  }
		  
		  private void getCert(Reader card){
			try {				
				KeyStore ks = card.loadPkcs11();
				
				java.security.cert.Certificate cert1 = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
			
				X509Certificate x509 = (X509Certificate) cert1;
		
				//Verifica validade do Certificado
				try {
				x509.checkValidity();
				} catch (CertificateExpiredException ve) {
					// TODO Auto-generated catch block
					valError = valError+ve.toString()+"; ";
					ve.printStackTrace();
				}
				
				
				String [] temp = null;
				System.out.println(x509.getSubjectDN());
			    temp = x509.getSubjectDN().getName().split(", ");
			    Map<String, String> tokens = new HashMap<String, String>();
			    
			    for (int i = 0 ; i < temp.length ; i++) {			    	
			    	String[] t= temp[i].split("=");
			    	tokens.put(t[0],t[1]);		    	
			    }

			    //Get Map in Set interface to get key and value
		        Set s=tokens.entrySet();

		        //Move next key and value of Map by iterator
		        Iterator it=s.iterator();

		        while(it.hasNext())
		        {
		            // key=value separator this by Map.Entry to get key and value
		            Map.Entry m =(Map.Entry)it.next();
		            // getKey is used to get key of Map
		            String key= (String) m.getKey();
		            // getValue is used to get value of key in Map
		            String value=(String)m.getValue();
		            //System.out.println("Key :"+key+"  Value :"+value);
		        }
		        certserial = x509.getSerialNumber().toString();
		        serialCertBI=tokens.get("SERIALNUMBER");
		        //System.out.println(serialCertBI);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   
	
			   
		  }
		
		  
		  
		  
		  
	  /**
	    * Loads the keystore from the smart card using its PKCS#11
	    * implementation library and the Sun PKCS#11 security provider.
	    * @return 
	    */
		private KeyStore loadPkcs11(){
			String[] t = System.getProperty("os.name").split(" ");
			//System.out.println(t[0]);
			//System.out.println(System.getProperty("os.arch"));

			String pkcs11ConfigSettings = null;
			if (t[0].equals("Windows")){
				 pkcs11ConfigSettings ="name = SmartCard\n" + "library = pteidpkcs11.dll";			
			}else {
				 pkcs11ConfigSettings ="name = SmartCard\n" + "library = /usr/local/lib/libpteidpkcs11.so";			
			}
			
			byte[] pkcs11configBytes = pkcs11ConfigSettings.getBytes();
			ByteArrayInputStream configStream = new ByteArrayInputStream(pkcs11configBytes);
			 
			final Provider p = new sun.security.pkcs11.SunPKCS11(configStream);
			Security.addProvider(p);
			KeyStore ks = null;
			
			try {
			
				ks = KeyStore.getInstance("PKCS11");				
				ks.load(null,null);
				 
				//for (Enumeration e =  ks.aliases(); e.hasMoreElements( );)
						//System.out.println("\t" + e.nextElement( ));
				
				return ks;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return ks;			 
			
		}


		  
}
