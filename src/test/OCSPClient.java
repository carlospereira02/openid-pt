package test;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;
import java.io.*;
import java.net.*;
 
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.CertificateStatus;
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
 
    public static void main(
        String[] args)
        throws Exception
    {	
 
    	
    	//Read user Certificate
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	X509Certificate interCert = (X509Certificate)getCert("CITIZEN AUTHENTICATION CERTIFICATE");
  
    	
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
        //String serviceAddr="http://ocsp.digsigtrust.com:80/";
        //String serviceAddr="http://ocsp.verisign.com";
        String serviceAddr="http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";
        
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
}
