package test;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;


public class DisplayCerts {
    KeyStore ks;
    private char[] pw;

    public static void main(String args[]) {
        try {
		
       	//System.load("/usr/local/lib/libpteidpkcs11.so");
       	//String pkcs11config = "name=CartaoCidadao\n"+"library=/usr/local/lib/libpteidpkcs11.so";
        String configName = "/home/luis/workspace/big/src/CC_pkcs11.cfg";
		Provider p = new sun.security.pkcs11.SunPKCS11(configName);
	    Security.addProvider(p);

	    KeyStore ks = KeyStore.getInstance("PKCS11");
	    ks.load(null,null);

	    System.out.println("\n\n");

	    for (Enumeration e = ks.aliases(); e.hasMoreElements();) {
		
		String alias = (String) e.nextElement(); 

		if (ks.isKeyEntry(alias)) {
		    System.out.println(alias + " is a key entry in the keystore");

		    System.out.println("The private key for " + alias + " is " + ks.getKey(alias, null));
		    java.security.cert.Certificate certs[] = ks.getCertificateChain(alias);
		    if (certs[0] instanceof X509Certificate) {
				X509Certificate x509 = (X509Certificate) certs[0];
				System.out.println(alias + " is really " + x509.getTBSCertificate());
			}
		    if (certs[0] instanceof X509Certificate) {
				X509Certificate x509 = (X509Certificate) certs[0];
				System.out.println(alias + " is really " + x509.toString());
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
	catch (Exception e) {
            e.printStackTrace();
        }
    }
}
