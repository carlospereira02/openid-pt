package test;

import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.*;
import java.util.*;
import java.io.*;

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
