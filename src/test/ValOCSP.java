package test;

import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.*;
import java.util.*;
import java.io.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


public class ValOCSP {
    private static final String TEST_RESPONDER_URL = "http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";
    /**
     * Sample params:
     * TDCOCESSTEST2.cer PIDTestBruger2.cer
     * @param args
     */
    public static void main(String [] args){
        try {
    	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                      
            String caFile = "Cartao de Cidadao 002.cer";
            FileInputStream isCertCA = new FileInputStream(caFile);
            X509Certificate caCert = (X509Certificate)cf.generateCertificate(isCertCA);
    		
            String certFile = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
            FileInputStream isCert = new FileInputStream(certFile);
            X509Certificate certSUBCA = (X509Certificate)cf.generateCertificate(isCert);
           
            byte[] tet = caCert.getEncoded();
            
            // Encode the byte array to a string
           // Base64Encoder en = new Base64Encoder();		
            String temp = Base64.encode(tet);
           
            System.out.println("Temp: " + temp.length());
            String abb= "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";
            String certSUBCAtxt= "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";
            
            System.out.println("aaa: " + abb.length());

            FileOutputStream fos = new FileOutputStream("caCert.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(temp);
            
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.decode(abb));
            X509Certificate cert2 = (X509Certificate) cf.generateCertificate(input);
            System.out.println("CERT2: " + cert2.getIssuerDN());

           
           X509Certificate clientCert =(X509Certificate) getCert("CITIZEN AUTHENTICATION CERTIFICATE");
            System.out.println(clientCert.getSerialNumber());
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
                System.out.println("Validation failure, cert["
                        + cpve.getIndex() + "] :"+ cpve.getMessage());
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CertificateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
    
    private static X509Certificate readCert(String fileName) throws FileNotFoundException, CertificateException {
        InputStream is = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(is);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
        return cert;
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
