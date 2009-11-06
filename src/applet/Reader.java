package applet;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
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
	public String hash = null;	
	byte[] signat = null;
	
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
			/**
			 * Gets personal data info from CC Card
			 * @param idData
			 */
		  private void PrintIDData(PTEID_ID idData)
		  {
		   name= idData.firstname+" "+idData.name;
		   numNIF= idData.numNIF;
		   numBI= idData.numBI;
		   //System.out.println("TESTE="+numBI);
		   birthDate= idData.birthDate;
		   
		  }
		  /**
		   * Gets address data info from CC Card
		   * @param adData
		   */
		  private void PrintADData(PTEID_ADDR adData)
		  {
		   street= adData.street;
		   cp4= adData.cp4;
		   cp3= adData.cp3;
		   municipality= adData.municipalityDesc;
		   streetType = adData.streettype;
		  }
		  /**
		   * returns CC card type
		   */
		  private void GetcardType(int card){
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
		  /**
		   * puts error info
		   */
		  private void setErrors(int error){
			  errors = error;			  
		  }
		  /**
		   * prints user data, just for test
		   */
		  private void printdata(){
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
			/**
			 * compare numBI data from cert and cc card	 		 	
			 */
		  private void verifyCert(String numBI){
			  if (serialCertBI.equals("BI"+numBI)){
				  valError=valError+"BI E CERTIFICADO CONFEREM!!";				  
			  }
			  else{				  
				  valError=valError+"BI E CERTIFICADO DIFEREM!! numBI="+numBI+"; serialBI="+serialCertBI;
			  }
			  
		  }
		  /**
		   * Creates a hash from user data info CC card
		   */
		  public String hashPassword(String password) {
			  String hashword = null;
			  try {
			  MessageDigest md5 = MessageDigest.getInstance("MD5");
			  md5.update(password.getBytes());
			  BigInteger hash = new BigInteger(1, md5.digest());
			  hashword = hash.toString(16);
			  } catch (NoSuchAlgorithmException nsae) {
			  // ignore
			  }
			  return hashword;
			  }
		  /**
		   * Get some data from user certificate
		   */
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
		            Map.Entry m =(Map.Entry)it.next();
		            String key= (String) m.getKey();
		            String value=(String)m.getValue();
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
				return ks;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				}			
			return ks;			 
			
		}
		/**
		 * Sign user data
		 * @param value
		 * @param card
		 */
		public static void signature(String value, Reader card) {
			  String data = value;
			try {		
				KeyStore ks = card.loadPkcs11();					
				PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN SIGNATURE CERTIFICATE", null);
				
				Signature s = Signature.getInstance("MD5withRSA");
				s.initSign(pk);
				
				byte buf[] = data.getBytes();
				s.update(buf);
				
				card.signat=(s.sign());
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		
		/**
		 * Main
		 * @param args
		 */
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
					     
					     card.hash = card.hashPassword(					    		 
					    		 card.name+
					    		 card.numNIF+
					    		 card.numBI+
					    		 card.birthDate+
					    		 card.certserial+
					    		 card.errors+
					    		 card.valError+
					    		 card.cardType+
					    		 card.serialCertBI+
					    		 card.street+
					    		 card.streetType+
					    		 card.cp4+
					    		 card.cp3+
					    		 card.municipality
					     );
					     
					     signature(card.hash, card);
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
			     
			     System.out.println("hash="+card.hash+card.valError);
		  }
		  
}
