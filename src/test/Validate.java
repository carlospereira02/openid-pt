package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Validate {

	/**
	 * @param args
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws CertPathValidatorException 
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidAlgorithmParameterException, CertPathValidatorException {
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		List mylist = new ArrayList();  
	 
		String caFile = "Cartao de Cidadao 002.cer";
        FileInputStream isCertCA = new FileInputStream(caFile);
        X509Certificate certCA = (X509Certificate)cf.generateCertificate(isCertCA);
		
        String certFile = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
        FileInputStream isCert = new FileInputStream(certFile);
        X509Certificate certSUBCA = (X509Certificate)cf.generateCertificate(isCert);
		
        X509Certificate cert =(X509Certificate) getCert("CITIZEN AUTHENTICATION CERTIFICATE");

        System.out.println(certCA.getIssuerDN());
        System.out.println(certSUBCA.getIssuerDN());
        System.out.println(cert.getIssuerDN());
	
		mylist.add(cert);
	    mylist.add(certSUBCA);
	   
	    
	    CertPath cp = cf.generateCertPath(mylist);
	    
	    Certificate trust = certCA;
	    TrustAnchor anchor = new TrustAnchor((X509Certificate) trust, null);
	    PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
	    params.setRevocationEnabled(false);
	    CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
	    PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
	    System.out.println(result);
	    
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
	final String pkcs11ConfigSettings ="name = SmartCard\n" + "library = /usr/local/lib/libpteidpkcs11.so";			

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
