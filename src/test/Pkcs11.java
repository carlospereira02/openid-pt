package test;
import java.io.*;
import java.security.*;
import java.util.Enumeration;


public class Pkcs11 {
	@SuppressWarnings("restriction")
	public static void main(String args[]) {
		  String data;
	      data = "This have I thought good to deliver thee, " +
	      "that thou mightst not lose the dues of rejoicing " +
	      "by being ignorant of what greatness is promised thee.";
		try {
			FileOutputStream fos = new FileOutputStream("test");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			 String configName = "/home/luis/workspace/big/src/CC_pkcs11.cfg";
			 Provider p = new sun.security.pkcs11.SunPKCS11(configName);
			 Security.addProvider(p);
			 //System.out.println("Provider   : " + p.getName());
			 //System.out.println("Provider   : " + p.getInfo());
			 //System.out.println("Provider   : " + p.getVersion());
			 //System.out.println("Provider   : " + p.getServices());

			 KeyStore ks = KeyStore.getInstance("PKCS11");
			 ks.load(null,null);
			 
			for (Enumeration e =  ks.aliases(); e.hasMoreElements( );)
					System.out.println("\t" + e.nextElement( ));
			
			PrivateKey pk = (PrivateKey) ks.getKey("CITIZEN SIGNATURE CERTIFICATE", null);
			
			Signature s = Signature.getInstance("MD5withRSA");
			s.initSign(pk);
			
			byte buf[] = data.getBytes();
			s.update(buf);
			
			oos.writeObject(data);
			oos.writeObject(s.sign());
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}

