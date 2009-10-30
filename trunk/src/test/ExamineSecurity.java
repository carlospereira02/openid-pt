package test;
import java.security.*;
import java.util.*;

public class ExamineSecurity {
	public static void main(String args[]) {
		try {
			Provider p[] = Security.getProviders( );
			for (int i = 0; i < p.length; i++) {
				System.out.println(p[i]);
				for (Enumeration e = p[i].keys(); e.hasMoreElements( );)
					System.out.println("\t" + e.nextElement( ));
			}
			}
			catch (Exception e) {
				System.out.println(e);
			}
	}
}
