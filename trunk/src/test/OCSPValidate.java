package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class OCSPValidate {

	public static void main(String[] args) {
		try {
		    CertPath cp = null;
		    Vector certs = new Vector();
		    URI ocspServer = null;
		    String url = "http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";
		   
		    // load the cert to be checked
		    certs.add(getCert("CITIZEN AUTHENTICATION CERTIFICATE"));
		    
		    // handle location of OCSP server
		    if (certs.size() == 1) {
			ocspServer = new URI("http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp");
		        System.out.println("Using the OCSP server at: http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp");
		        System.out.println("to check the revocation status of: " +
			    certs.elementAt(0));
		        System.out.println();
		    } else {
		        System.out.println("Using the OCSP server specified in the " +
			    "cert to check the revocation status of: " +
			    certs.elementAt(0));
		        System.out.println();
		    }
		    // init cert path
		    CertificateFactory cf = CertificateFactory.getInstance("X509");
		    cp = (CertPath)cf.generateCertPath(certs);
		   
		    
		    // load the root CA cert for the OCSP server cert
		    String caFile = "Cartao de Cidadao 002.cer";
	        FileInputStream isCertCA = new FileInputStream(caFile);
		    X509Certificate rootCACert = (X509Certificate)cf.generateCertificate(isCertCA);

	        String certFile = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
	        FileInputStream isCert = new FileInputStream(certFile);
	        X509Certificate ocspCert = (X509Certificate)cf.generateCertificate(isCert);
	        
	        // init trusted certs
		    TrustAnchor ta = new TrustAnchor(rootCACert, null);
		    Set trustedCertsSet = new HashSet();
		    trustedCertsSet.add(ta);
		  
		    // init cert store
		    Set certSet = new HashSet();
		    certSet.add(rootCACert);
		    CertStoreParameters storeParams = new CollectionCertStoreParameters(certSet);
		    CertStore store = CertStore.getInstance("Collection", storeParams);
		   
		    // init PKIX parameters
	        PKIXParameters params = null;
		    params = new PKIXParameters(trustedCertsSet);
		    params.addCertStore(store);
		    // enable OCSP
		    Security.setProperty("ocsp.enable", "true");
		    if (ocspServer != null) {
			Security.setProperty("ocsp.responderURL", url);
			Security.setProperty("ocsp.responderCertSubjectName", ocspCert.getSubjectX500Principal().getName());
		    }
		    // perform validation
		    CertPathValidator cpv = CertPathValidator.getInstance("PKIX");


		    PKIXCertPathValidatorResult cpv_result  = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
		    X509Certificate trustedCert = (X509Certificate)
			cpv_result.getTrustAnchor().getTrustedCert();
		    if (trustedCert == null) {
			System.out.println("Trsuted Cert = NULL");
		    } else {
			System.out.println("Trusted CA DN = " +
			    trustedCert.getSubjectDN());
		    }
		} catch (CertPathValidatorException e) {
		    e.printStackTrace();
		    System.exit(1);
		} catch(Exception e) {
		    e.printStackTrace();
		    System.exit(-1);
		}


		System.out.println("CERTIFICATE VALIDATION SUCCEEDED");
		System.exit(0);
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
