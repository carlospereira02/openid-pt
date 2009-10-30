package main;
	/**
	 * <p>Title: pteidlib JNI Test</p>
	 * <p>Description: Test pteidlib jni interface</p>
	 * <p>Copyright: Copyright (c) 2007</p>
	 * <p>Company: Zetes</p>
	 * @author not attributable
	 * @version 1.0
	 */

	import pteidlib.*;
import java.math.BigInteger;

	
	public class Card {	
	  static
	  {
	    try
	    {
	 	
	    	System.loadLibrary("pteidlibj");
	    }
	    catch (UnsatisfiedLinkError e)
	    {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	  }

	 
	  public static void main(String[] args)
	  {
		  Card card = new Card();
		    try
		    {					
		    	//card.TestCVC();
		    	
		    	//check reader and card
		    	pteid.Init("");
		    	//pteid.CVC_Init();
		    	try {
					card.TestCVC();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    } 
		     catch(PteidException e)
		     	{
		           e.printStackTrace();
		           System.out.println(e.getMessage());
		           String msg = e.getMessage();

		           msg = msg.substring(14);
		           int cod = Integer.parseInt(msg);
		       
		           switch(cod){
		           case 1104:
		        	   System.out.println("Cartão Não Presente");
		        	   break;
		           case 1101:
		        	   System.out.println("Não foram encontrados leitores");
		        	   break;
		           case 1210:
		        	   System.out.println("Cartão Inválido");
		        	   break;
		           }
		         }
		     
		     //validate card data
		   
		   PTEID_Certif[] certs=  pteid.GetCertificates();
		   PTEID_Certif cert = certs[1];
		 
		   
	  }

	  
	  //////////////PTEID_Certif////////////////////CVC ////////////////////////////////////

		// Modify these values for you test card!
		static String csCvcCertRole01 = "7F2181CD5F37818086B81455BCF0F508CF79FE9CAD574CA631EBC392D78869C4DC29DB193D75AC7E1BB1852AA57FA54C7E7FA97CBB536F2FA384C90C4FF62EAB119156016353AEAFD0F2E2B41BF89CCFE2C5F463A4A30DC38F2B9145DA3F12C40E2F394E7EE606A4C9377253D6E46D7B538B34C712B964F4A20A5724E0F6E88E0D5D1188C39B75A85F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for writing
		static String csCvcCertRole02 = "7F2181CD5F3781804823ED79D2F59E61E842ABE0A58919E63F362C9133E873CA77DD79AD01009247460DFE0294DD0ABAABE1D262E69A165F2F1AC6E953E8ABBE3BF1D2ACD6EB69EE83AB918D6F5116589BE0D40E780D5635238B78AA4290AD32F2A6316D24B417E06591DE6A775C38CFD918CA4FD11146EA20E06FE7F73CA7B3D3058FA259745D875F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for reading
		static String csCvcMod = "924557F6E1C2F1898B391D9255CC72FD7F11128BA148CFEBD1F58AF3F363778157E262FD72A76BCCA0AB43D8F5272E00D21B8B0EE4CC7DA86C8189DEC0DDC58C6A54A81BCE5E52076917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D"; // private key modulus
		static String csCvcExp = "3B35A8CAFE4E6C79D20AB7C6C1C67611D97AEEB7E8FCD175D353030187578F4BA368B7CB82BAF4EF2B66C89B2D79C3AC7F60B8E4B98771A258F202FE51B23441EB29C68569B608EF1F4B3CF15C68744AA7A3800E364739D3C6DCB078EFB81EA3197C843EE17BD9BCF1E0FEB4FFB6719F923C63105206A2F5A77A0437D762E781"; // private key exponent

		/**
		 * BigInteger -> bytes array, taking into account that a leading 0x00 byte
		 * can be added if the MSB is 1 (so we have to remove this 0x00 byte)
		 */
		byte[] ToBytes(BigInteger bi, int size)
		{
			byte[] b = bi.toByteArray();
			if (b.length == size)
				return b;
			else if (b.length != size + 1) {
				System.out.println("length = " + b.length + " instead of " + size);
				return null;
			}
			byte[] r = new byte[size];
			System.arraycopy(b, 1, r, 0, size);
			return r;
		}

		public byte[] SignChallenge(byte[] challenge)
		{
			BigInteger mod = new BigInteger("00" + csCvcMod, 16);
			BigInteger exp = new BigInteger("00" + csCvcExp, 16);
			BigInteger chall = new BigInteger(1, challenge);

			BigInteger signat = chall.modPow(exp, mod);

			return ToBytes(signat, 128);
		}
		
		
	  public void TestCVC() throws Exception
		{
			/* Convert a hex string to byte[] by using the BigInteger class,
			 * taking into account that the MSB is taken to be the sign bit
			 * (hence adding the "00")
			 * CVC certs are always 209 bytes long */
			byte[] cert1 = ToBytes(new BigInteger("00" + csCvcCertRole01, 16), 209);
			byte[] cert2 = ToBytes(new BigInteger("00" + csCvcCertRole02, 16), 209);

			byte[] fileAddr = { 0x3F, 0x00, 0x5F, 0x00, (byte)0xEF, 0x05 };

			// Read the Address file

			pteid.Init("");
			pteid.SetSODChecking(false);

			byte[] challenge = pteid.CVC_Init(cert2);
			byte[] signat = SignChallenge(challenge);
			pteid.CVC_Authenticate(signat);

			PTEID_ADDR addr = pteid.CVC_GetAddr();
			String country = addr.country;
			System.out.println("Reading address:");
			System.out.println("  addrType = " + addr.addrType);
			System.out.println("  country = " + country);

			pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

			// Write to the Address file

			System.out.println("Changing country name to \"XX\"");

			pteid.Init("");
			pteid.SetSODChecking(false);

			challenge = pteid.CVC_Init(cert1);
			signat = SignChallenge(challenge);
			pteid.CVC_Authenticate(signat);

			addr.country = "XX";
			pteid.CVC_WriteAddr(addr);

			pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

			System.out.println("  done");

			// Read the Address file again

			pteid.Init("");
			pteid.SetSODChecking(false);

			challenge = pteid.CVC_Init(cert2);
			signat = SignChallenge(challenge);
			pteid.CVC_Authenticate(signat);

			addr = pteid.CVC_GetAddr();
			System.out.println("Reading address again:");
			System.out.println("  addrType = " + addr.addrType);
			System.out.println("  country = " + country);

			pteid.Exit(pteid.PTEID_EXIT_UNPOWER);
			
			// Restore the previous address

			System.out.println("Restoring country name");

			pteid.Init("");
			pteid.SetSODChecking(false);

			challenge = pteid.CVC_Init(cert1);
			signat = SignChallenge(challenge);
			pteid.CVC_Authenticate(signat);

			addr.country = country;
			pteid.CVC_WriteAddr(addr);

			pteid.Exit(pteid.PTEID_EXIT_UNPOWER);

			System.out.println("  done");
		}
	}