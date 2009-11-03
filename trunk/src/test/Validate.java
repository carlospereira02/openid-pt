package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidAlgorithmParameterException, CertPathValidatorException, URISyntaxException {
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		List mylist = new ArrayList();  
	    URI ocspServer = new URI ("http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp");

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
	   
	    // enable OCSP
	    Security.setProperty("ocsp.enable", "true");
	    if (ocspServer != null) {
		Security.setProperty("ocsp.responderURL", ocspServer.getPath());
		Security.setProperty("ocsp.responderCertSubjectName","OCSP");
	    }
	    
	    CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
	    //System.out.println(cpv.getProvider());

	    PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
	    //System.out.println(result);
	    
	    X509Certificate trustedCert = (X509Certificate)	result.getTrustAnchor().getTrustedCert();
	    if (trustedCert == null) {	  
		System.out.println("Trsuted Cert = NULL");
	    } else {
		System.out.println("Trusted CA DN = " +
		    trustedCert.getSubjectDN());
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
