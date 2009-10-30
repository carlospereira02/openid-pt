package test;

// How to export the private key from keystore?
// Does keytool not have an option to do so?
// This example use the "testkeys" file that comes with JSSE 1.0.3
 
import sun.misc.BASE64Encoder;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.*;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
 
class ExportPriv {
    public static void main(String args[]) throws Exception{
	ExportPriv myep = new ExportPriv();
	myep.doit();
    }
 
    public void doit() throws Exception{
    	String configName = "/home/luis/workspace/big/src/CC_pkcs11.cfg";
    	Provider p = new sun.security.pkcs11.SunPKCS11(configName);
 	    Security.addProvider(p);

 	    KeyStore ks = KeyStore.getInstance("PKCS11");
 	    ks.load(null,null);
		
 	    PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN SIGNATURE CERTIFICATE",null);
 	   
 	    char[] passPhrase = "passphrase".toCharArray();
 	    BASE64Encoder myB64 = new BASE64Encoder();
	
 	  // String b64 = myB64.encode(pk.getEncoded());
 
 	   for (Enumeration e = ks.aliases(); e.hasMoreElements();) {
 			
 			String alias = (String) e.nextElement(); 

 			if (ks.isKeyEntry(alias)) {
 			    System.out.println(alias + " is a key entry in the keystore");

 			    System.out.println("The private key for " + alias + " is " + ks.getKey(alias, null));
 			    java.security.cert.Certificate certs[] = ks.getCertificateChain(alias);
 			    
 			    if (certs[0] instanceof X509Certificate) {
 					X509Certificate x509 = (X509Certificate) certs[0];
 					//PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN SIGNATURE CERTIFICATE",null);
 					System.out.println("-----BEGIN PUBLIC KEY-----");
 					//String b64 = myB64.encode(pk.getEncoded());
 					//System.out.println(b64);
 					System.out.println("-----END PUBLIC KEY-----");

 					//System.out.println(alias + " is really " + x509.getTBSCertificate());
 				}
 			    if (certs[0] instanceof X509Certificate) {
 					X509Certificate x509 = (X509Certificate) certs[0];
 					System.out.println(alias + " is really " + (RSAPublicKey) x509.getPublicKey());
;
 				}
 			    if (certs[0] instanceof X509Certificate) {
 				X509Certificate x509 = (X509Certificate) certs[0];
 				System.out.println(alias + " is really " + x509.getSubjectDN());
 			    }

 			    if (certs[certs.length - 1] instanceof X509Certificate) {
 				X509Certificate x509 = (X509Certificate) certs[certs.length - 1];
 				System.out.println(alias + " was verified by " +x509.getIssuerDN());
 			    }
 			}
 			    
 		    else 
 			if (ks.isCertificateEntry(alias)) {
 			    System.out.println(alias + " is a certificate entry in the keystore");
 			    java.security.cert.Certificate c = ks.getCertificate(alias);
 			    if (c instanceof X509Certificate) {
 				X509Certificate x509 = (X509Certificate) c;
 				System.out.println(alias + " is really " + x509.getSubjectDN());
 				System.out.println(alias + " was verified by " + x509.getIssuerDN());
 			    }
 			}


 			System.out.println("\n\n");

 		    }
	
	
 
	}
 
// From http://javaalmanac.com/egs/java.security/GetKeyFromKs.html
 
   public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
        try {
            // Get private key
            Key key = keystore.getKey(alias, password);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);
    
                // Get public key
                PublicKey publicKey = cert.getPublicKey();
    
                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey)key);
            }
        } catch (UnrecoverableKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        }
        return null;
    }
 
}
 

