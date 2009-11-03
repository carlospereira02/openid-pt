package test;

/**
 * @author Xuelei Fan
 */
import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.*;

public class AuthorizedResponderNoCheck {

       private static CertPath generateCertificatePath()
            throws CertificateException, FileNotFoundException {
        // generate certificate from cert strings
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // load the root CA cert for the OCSP server cert
	    String caFile = "Cartao de Cidadao 002.cer";
        FileInputStream isCertCA = new FileInputStream(caFile);
	    Certificate trusedCert = (Certificate)cf.generateCertificate(isCertCA);

        String certFile = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
        FileInputStream isCert = new FileInputStream(certFile);
        Certificate issuerCert = (Certificate)cf.generateCertificate(isCert);
        
          
        Certificate targetCert = getCert("CITIZEN AUTHENTICATION CERTIFICATE");

        // generate certification path
        List list = Arrays.asList(new Certificate[] {
                        targetCert, issuerCert, trusedCert});

        return cf.generateCertPath(list);
    }

    private static Set generateTrustAnchors()
            throws CertificateException, FileNotFoundException {
        // generate certificate from cert string
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        String caFile = "Cartao de Cidadao 002.cer";
        FileInputStream isCertCA = new FileInputStream(caFile);
        Certificate selfSignedCert= (Certificate)cf.generateCertificate(isCertCA);
	    
        // generate a trust anchor
        TrustAnchor anchor =
            new TrustAnchor((X509Certificate)selfSignedCert, null);

        return Collections.singleton(anchor);
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
    public static void main(String args[]) throws Exception {

        // if you work behind proxy, configure the proxy.
        System.setProperty("http.proxyHost", "proxyhost");
        System.setProperty("http.proxyPort", "proxyport");

        CertPath path = generateCertificatePath();
        Set anchors = generateTrustAnchors();

        PKIXParameters params = new PKIXParameters(anchors);

        // Activate certificate revocation checking
        params.setRevocationEnabled(true);

        // Activate OCSP
        Security.setProperty("ocsp.enable", "true");

        // Activate CRLDP
        System.setProperty("com.sun.security.enableCRLDP", "true");

        // Ensure that the ocsp.responderURL property is not set.
        if (Security.getProperty("ocsp.responderURL") != null) {
            throw new
                Exception("The ocsp.responderURL property must not be set");
        }
        System.out.println(Security.getProperty("ocsp.responderURL"));
        
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");

        validator.validate(path, params);
    }
}