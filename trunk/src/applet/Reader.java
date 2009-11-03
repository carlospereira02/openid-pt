package applet;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;

public class Reader {
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
		    System.out.println("DeliveryEntity : " + idData.deliveryEntity);
		    System.out.println("PAN : " + idData.cardNumberPAN);
		    System.out.println("...");
		  }
		 
		  public static void main(String[] args)
		  {
			  Reader card = new Reader();
			    try
			    {					
			    	//card.TestCVC();
			    	
			    	//check reader and card
			    	pteid.Init("");			 
			    	
			    	int cardtype = pteid.GetCardType();
			    	switch (cardtype)
			   	  	{
			   	  	case pteid.CARD_TYPE_IAS07:
			   	  		System.out.println("IAS 0.7 card\n");
			   	  		break;
			   		case pteid.CARD_TYPE_IAS101:
			   			System.out.println("IAS 1.0.1 card\n");
			   			break;
			   		case pteid.CARD_TYPE_ERR:
			   			System.out.println("Unable to get the card type\n");
			   			break;
			   		default:
			   			System.out.println("Unknown card type\n");
			   	  }
			    	
			    	// Read ID Data
			    	PTEID_ID idData = pteid.GetID();
					 if (null != idData)
					    {
						card.PrintIDData(idData);
					    }
			    
			    } 
			     catch(PteidException e)
			     	{
			           e.printStackTrace();
			           System.out.println(e.getMessage());
			           String msg = e.getMessage();

			           msg = msg.substring(14);
			           int cod = Integer.parseInt(msg);
			       
			           switch(cod){
			           case 1104:
			        	   System.out.println("Cartão Não Presente");
			        	   break;
			           case 1101:
			        	   System.out.println("Não foram encontrados leitores");
			        	   break;
			           case 1210:
			        	   System.out.println("Cartão Inválido");
			        	   break;
			           }
			         }
			     
			//validate card data
			   
			    
			
			try {				
				KeyStore ks = loadPkcs11();
				
				java.security.cert.Certificate cert1 = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
			
				String formato = cert1.getPublicKey().getFormat();
				
				X509Certificate x509 = (X509Certificate) cert1;
				
				System.out.println(formato);
				System.out.println(x509.getIssuerX500Principal());
				
				//Verifica validade do Certificado
				try {
				x509.checkValidity();
				} catch (CertificateExpiredException ve) {
					// TODO Auto-generated catch block
					ve.printStackTrace();
				}
				
				//System.out.println(x509.getTBSCertificate());
				
				String [] temp = null;
			    temp = x509.getSubjectDN().getName().split(", ");
			    Map<String, String> tokens = new HashMap<String, String>();
			    
			    for (int i = 0 ; i < temp.length ; i++) {
			    	//System.out.println(temp.length);
			    	//System.out.println(temp[i]);
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

		            System.out.println("Key :"+key+"  Value :"+value);
		        }
		        
				System.out.println(x509.getSubjectDN());
				
				
			    
			    
			 
				//byte[] derBytes = x509.getEncoded();
    

				
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

		private static KeyStore loadPkcs11(){
			String[] t = System.getProperty("os.name").split(" ");
			System.out.println(t[0]);
			System.out.println(System.getProperty("os.arch"));

			String pkcs11ConfigSettings = null;
			if (t[0].equals("Windows")){
				 pkcs11ConfigSettings ="name = SmartCard\n" + "library = C:\\Windows\\SysWOW64\\pteidpkcs11.dll";			
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
				 
				for (Enumeration e =  ks.aliases(); e.hasMoreElements( );)
						System.out.println("\t" + e.nextElement( ));
				
				return ks;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return ks;			 
			
		}


		  
}
