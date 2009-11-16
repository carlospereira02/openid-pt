package applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import pteidlib.PTEID_ADDR;
import pteidlib.PTEID_ID;
import pteidlib.PteidException;
import pteidlib.pteid;

//No need to extend JApplet, since we don't add any components;
//we just paint.
public class Simple extends Applet {
	public String name = null;
	public String numNIF = null;
	public String numBI = null;
	public String birthDate = null;
	public String certserial = null;
	public int errors = 0;
	public static String valError = "";
	public String cardType = null;
	public String serialCertBI = null;
	public String   street= null;
	public String   streetType= null;
	public String   cp4= null;
	public String   cp3= null;
	public String   municipality= null;
	public String hash = null;	
	static byte[] signat = null;
    private static final String TEST_RESPONDER_URL = "http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";

    StringBuffer buffer;
    
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
  /**	public String name = null;
	public String numNIF = null;
	public String numBI = null;
	public String birthDate = null;
	public String certserial = null;
	public int errors = 0;
	public static String valError = "";
	public String cardType = null;
	public String serialCertBI = null;
	public String   street= null;
	public String   streetType= null;
	public String   cp4= null;
	public String   cp3= null;
	public String   municipality= null;
	public String hash = null;	
	byte[] signat = null;
    private static final String TEST_RESPONDER_URL = "http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";

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
  private void getCert(){
	try {				
		KeyStore ks = loadPkcs11();
		
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
   * validateCertPath
   * @param certList
   * @param trustedCert
   * @param responderUrl
   */
  private static void validateCertPath(List certList, X509Certificate trustedCert, String responderUrl) {
        try {
            // Instantiate a CertificateFactory for X.509
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            // Extract the certification path from
            // the List of Certificates
            CertPath cp = cf.generateCertPath(certList);

            // Create CertPathValidator that implements the "PKIX" algorithm
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");

            // Set the Trust anchor
            TrustAnchor anchor = new TrustAnchor(trustedCert, null);

            // Set the PKIX parameters
            PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
            params.setRevocationEnabled(true);
            // the list of additional signer certificates for populating the trust store
            //System.setProperty("com.sun.security.enableCRLDP", "false");
            Security.setProperty("ocsp.enable", "true");
            Security.setProperty("ocsp.responderURL", responderUrl);

            // Validate and obtain results
            try {
                PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
                PolicyNode policyTree = result.getPolicyTree();
                PublicKey subjectPublicKey = result.getPublicKey();

                System.out.println("Certificate validated");
                System.out.println("Policy Tree:\n" + policyTree);
                System.out.println("Subject Public key:\n" + subjectPublicKey);

            } catch (CertPathValidatorException cpve) {
            	 valError=valError+"; "+cpve.getMessage();	
                System.out.println("Validation failure, cert["
                        + cpve.getIndex() + "] :"+ cpve.getMessage()+": "+cpve.getCause());
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CertificateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
  
/**
* Loads the keystore from the smart card using its PKCS#11
* implementation library and the Sun PKCS#11 security provider.
* @return 
*/
private static KeyStore loadPkcs11(){
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
 * Validate certs via OCSP
 * @param card
 */
private void ocspval(){
	 try {
    	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String ca= "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";

            ByteArrayInputStream isCertCA = new ByteArrayInputStream(Base64.decode(ca));
            X509Certificate caCert = (X509Certificate)cf.generateCertificate(isCertCA);
            //System.out.println(caCert.getSubjectDN());
            String certSUBCAtxt= "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";
            ByteArrayInputStream isCert = new ByteArrayInputStream(Base64.decode(certSUBCAtxt));
            X509Certificate certSUBCA = (X509Certificate)cf.generateCertificate(isCert);
            //System.out.println(certSUBCA.getSubjectDN());
        	KeyStore ks = loadPkcs11();					
			java.security.cert.Certificate cert1 = ks.getCertificate("CITIZEN AUTHENTICATION CERTIFICATE");
			X509Certificate clientCert = (X509Certificate) cert1;
			//System.out.println(clientCert.getSubjectDN());
			// System.out.println("clientCert.getSerialNumber"+clientCert.getSerialNumber());
            List certList = new Vector();
           // NB: this is the correct sequence!!
            certList.add(clientCert);
            certList.add(certSUBCA);
            certList.add(caCert);
            validateCertPath(certList, caCert, TEST_RESPONDER_URL);
        } catch (Exception e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
	
}
/**
 * Sign user data
 * @param value
 * @param card
 */
public static void signature(String value) {
	  String data = value;
	try {		
		KeyStore ks = loadPkcs11();					
		PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN SIGNATURE CERTIFICATE", null);
		
		Signature s = Signature.getInstance("MD5withRSA");
		s.initSign(pk);
		
		byte buf[] = data.getBytes();
		s.update(buf);
		
		signat=(s.sign());
	}
	catch (Exception e) {
		System.out.println(e);
	}
}



public void init() {
    buffer = new StringBuffer();
    addItem("initializing... ");
    try
    {					
    	//check reader and card
    	pteid.Init("");			 
        pteid.SetSODChecking(false);
        
        }    
    catch(PteidException e)
    {
    	e.printStackTrace();
    	String msg = e.getMessage();
    	msg = msg.substring(14);
    	//inform card error
    	setErrors(Integer.parseInt(msg));   
    }  
    
}

public void start() {
    addItem("starting... ");
    int val;
	try {
		val = pteid.IsActivated();
		if(val != 0){
	    	test();
	    }else
		{
			valError = "Cartão não activado!";
			
		}
		
	} catch (PteidException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    System.out.println(valError);

}

public void stop() {
    addItem("stopping... ");
}

public void destroy() {
    addItem("preparing for unloading...");
}

private void addItem(String newWord) {
    System.out.println(newWord);
    buffer.append(newWord);
    repaint();
}

private void test(){
	  //read card type
	int cardtype;
	try {
		cardtype = pteid.GetCardType();
		GetcardType( cardtype);
		   
		// Read ID Data
		PTEID_ID idData = pteid.GetID();
		 if (null != idData)
		    {
			PrintIDData(idData);
		    }
		 
		// Read ADDRESS Data
		 PTEID_ADDR adData = pteid.GetAddr();
			 if (null != adData)
			    {
				PrintADData(adData);
			    }
		
		 //validate cert data
	     getCert();
	     verifyCert(numBI);
	     printdata();
	     
	     hash = hashPassword(					    		 
	    		 name+
	    		 numNIF+
	    		 numBI+
	    		 birthDate+
	    		 certserial+
	    		 errors+
	    		 valError+
	    		cardType+
	    		 serialCertBI+
	    		 street+
	    		 streetType+
	    		 cp4+
	    		 cp3+
	    		 municipality
	     );
	     ocspval();
	     signature(hash);
	} catch (PteidException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	
	stop();
}   
public void paint(Graphics g) {
	
		
		g.drawRect(0, 0, 
		getSize().width - 1,
		getSize().height - 1);
		//Draw the current string inside the rectangle.
        g.drawString(buffer.toString(), 5, 15);
	
		   

	}
}
