package test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedInputStream.*;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPReq;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.SingleResp;
public class OCSPClient
{
    public static OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) 
        throws OCSPException
    {
    	    	
    	//Add provider BC
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
 
        // Generate the id for the certificate we are looking for
        CertificateID   id = new CertificateID(CertificateID.HASH_SHA1, issuerCert, serialNumber);
 
        // basic request generation with nonce
        OCSPReqGenerator    gen = new OCSPReqGenerator();
        
        gen.addRequest(id);
        
        // create details for nonce extension
        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        Vector     oids = new Vector();
        Vector     values = new Vector();
        
        oids.add(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
        values.add(new X509Extension(false, new DEROctetString(nonce.toByteArray())));
        
        gen.setRequestExtensions(new X509Extensions(oids, values));
 
        return gen.generate();
    }
 
    public static void main(String[] args)throws Exception  {	
    	//URI OCSP
    	String serviceAddr=null;
    	
    	//Read user Certificate
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	X509Certificate interCert = (X509Certificate)getCert("CITIZEN AUTHENTICATION CERTIFICATE");
    	//System.out.println(interCert);
    	byte[] value =  interCert.getExtensionValue("1.3.6.1.5.5.7.1.1");
       
    	AuthorityInformationAccess authorityInformationAccess;
        try {
    	DEROctetString oct = (DEROctetString) (new ASN1InputStream(  new ByteArrayInputStream(value)).readObject());
    	authorityInformationAccess = new AuthorityInformationAccess((ASN1Sequence) new ASN1InputStream(oct.getOctets()).readObject());
        } catch (IOException e) {
        	throw new RuntimeException("IO error: " + e.getMessage(), e);
        }
    	
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {
        	
        	GeneralName gn = accessDescription.getAccessLocation();
        	
        	DERIA5String str = DERIA5String.getInstance(gn.getDERObject());
        	String accessLocation = str.getString();
        	serviceAddr = accessLocation;
        	System.out.println("URI: "+accessLocation);
        }

    	//Read CA Certificate
    	  String caFile = "Cartao de Cidadao 002.cer";
          FileInputStream isCertCA = new FileInputStream(caFile);
          X509Certificate rootCert = (X509Certificate)cf.generateCertificate(isCertCA);
          isCertCA.close();
    	
    	
    	OCSPReq request = generateOCSPRequest(rootCert, interCert.getSerialNumber());
     
        //Codificate request:
        byte[] array = request.getEncoded();
 
        //Send request:
        //serviceAddr URL OCSP service

        String hostAddr="";
        if (serviceAddr != null) {
          hostAddr = serviceAddr;
          try {
            if (serviceAddr.startsWith("http")) {
              HttpURLConnection con = null;
              URL url = new URL((String) serviceAddr);
              con = (HttpURLConnection) url.openConnection();
              con.setRequestProperty("Content-Type", "application/ocsp-request");
              con.setRequestProperty("Accept", "application/ocsp-response");
              con.setDoOutput(true);
              OutputStream out = con.getOutputStream();
              DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
              //Escribo el request
              dataOut.write(array);
              
              dataOut.flush();
              dataOut.close();
            
              //Check errors in response:
              if (con.getResponseCode() / 100 != 2) {
                throw new Exception("***Error***");
              }
              
              //Get Response
              InputStream in = (InputStream) con.getContent();
              System.out.println("-------------------------------------------");
             
              System.out.println("Contenttype:"+con.getContentType() );
              System.out.println("Responsemessage:"+con.getResponseMessage() );
              System.out.println("Contenencoding:"+con.getContentEncoding() );
              System.out.println("headerFields:"+con.getHeaderFields() );
             
             // Object test =  new ASN1InputStream(in).readObject();
              try
              {
              File f=new File("outFile.txt");
              OutputStream saida=new FileOutputStream(f);
              byte buf[]=new byte[1024];
              int len;
              while((len=in.read(buf))>0)
              saida.write(buf,0,len);
              saida.close();
              in.close();
              System.out.println("\nFile is created...................................");
              }
              catch (IOException e){}
              
              System.out.println("response:"+con.getResponseCode() );
              System.out.println("response:");     
              
              
              System.out.println("-------------------------------------------");
            
              
              OCSPResp ocspResponse = new OCSPResp(in);
              
              /**
              ... DECODING THE RESPONSE [2] ...
              */
              System.out.println("OCSP response: "+ ocspResponse.getStatus() );

              BasicOCSPResp basicResponse = (BasicOCSPResp)ocspResponse.getResponseObject();
              if (basicResponse != null) {
            	SingleResp[] responses = basicResponse.getResponses();
            		    if (responses.length == 1) {
            		        SingleResp resp = responses[0];
            		         System.out.println("Status: " + resp.getCertStatus());
            		         System.out.println("This Update: " + resp.getThisUpdate());
            		        System.out.println("Next Update: " + resp.getNextUpdate());
            		         Object status = resp.getCertStatus();
            		         System.out.println (status.toString());
            		    }}
              
              
              System.out.println("ocsp: "+ocspResponse.getStatus());
            
            }
            else {
            	//HTTPS
            	//HttpsURLConnection
            	//...
            }
          }
          catch (Exception e) {
            System.out.println(e);
          }
        }
 
    }
    
    private static Certificate getCert(String alias){
		   Certificate  cert = null;
		           try {
						KeyStore ks = loadPkcs11();
						cert =  ks.getCertificate(alias);
		             } catch(Exception e) {
		             System.out.println("Can't construct X509 Certificate. " +
		             e.getMessage());
		         }
		             return cert;

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
	   
	   
	   public static String convertStreamToString(InputStream is) {
		           /*
		           * To convert the InputStream to String we use the BufferedReader.readLine()
		            * method. We iterate until the BufferedReader return null which means
		           * there's no more data to read. Each line will appended to a StringBuilder
		            * and returned as String.
		           */
		          BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		          StringBuilder sb = new StringBuilder();
		   
		          String line = null;
		          try {
		            while ((line = reader.readLine()) != null) {
		                   sb.append(line + "\n");
		              }
		           } catch (IOException e) {
		              e.printStackTrace();
		          } finally {
		              try {
		                   is.close();
		            } catch (IOException e) {
		                   e.printStackTrace();
		              }
		           }
		    
		           return sb.toString();
		       }
}
