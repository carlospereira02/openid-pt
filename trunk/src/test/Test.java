package test;
/**
 * <p>Title: pteidlib JNI Test</p>
 * <p>Description: Test pteidlib jni interface</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Zetes</p>
 * @author not attributable
 * @version 1.0
 */

import java.io.*;
import pteidlib.*;
import java.math.BigInteger;

public class Test
{
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

  public void PrintIDData(PTEID_ID idData)
  {
    System.out.println("DeliveryEntity : " + idData.deliveryEntity);
    System.out.println("PAN : " + idData.cardNumberPAN);
    System.out.println("...");
  }

  public void PrintAddressData(PTEID_ADDR adData)
  {
	if("N".equals(adData.addrType))
    {
        System.out.println("Type : National");
	System.out.println("Street : " + adData.street);
        System.out.println("Municipality : " + adData.municipality);
        System.out.println("...");
    }
    else
    {
        System.out.println("Type : International");
	System.out.println("Address : " + adData.addressF);
        System.out.println("City : " + adData.cityF);
        System.out.println("...");
    }
  }

  public static void main(String[] args)
  {
    Test test = new Test();
    try
    {
      test.TestCVC();

      pteid.Init("");

	  //test.TestChangeAddress();

		// Don't check the integrity of the ID, address and photo (!)
      pteid.SetSODChecking(false);

	  int cardtype = pteid.GetCardType();
	  switch (cardtype)
	  {
		  case pteid.CARD_TYPE_IAS07:
			  System.out.println("IAS 0.7 card\n");
			  break;
		  case pteid.CARD_TYPE_IAS101:
			  System.out.println("IAS 1.0.1 card\n");
			  break;
		  case pteid.CARD_TYPE_ERR:
			  System.out.println("Unable to get the card type\n");
			  break;
		  default:
			  System.out.println("Unknown card type\n");
	  }

      // Read ID Data
      PTEID_ID idData = pteid.GetID();
      if (null != idData)
      {
        test.PrintIDData(idData);
      }

	  // Read Address
      PTEID_ADDR adData = pteid.GetAddr();
      if (null != adData)
      {
        test.PrintAddressData(adData);
      }

	  // Read Picture Data
      /*
      PTEID_PIC picData = pteid.GetPic();
      if (null != picData)
      {
        try
        {
           String photo = "photo.jp2";
           FileOutputStream oFile = new FileOutputStream(photo);
           oFile.write(picData.picture);
           oFile.close();
           System.out.println("Created " + photo);
         }
         catch (FileNotFoundException excep)
         {
           System.out.println(excep.getMessage());
         }
         catch(Exception e)
         {
           e.printStackTrace();
         }
      }

       // PIN operations
       int triesLeft = pteid.VerifyPIN((byte)0x83, null);
       triesLeft = pteid.ChangePIN((byte)0x83, null, null);
       */
      
       // Read Certificates
       PTEID_Certif[] certs = pteid.GetCertificates();
       System.out.println("Number of certs found: " + certs.length);

       // Read Pins
       PTEID_Pin[] pins = pteid.GetPINs();

       // Read TokenInfo
       PTEID_TokenInfo token = pteid.GetTokenInfo();

       // Read personal Data
       byte[] filein = {0x3F, 0x00, 0x5F, 0x00, (byte)0xEF, 0x07};
       byte[] file = pteid.ReadFile(filein, (byte)0x81);

       // Write personal data
       String data = "Hallo JNI";
       pteid.WriteFile(filein, data.getBytes(), (byte)0x81);

       pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD);
    }
	catch (PteidException ex)
	{
		ex.printStackTrace();
		//System.out.println(ex.getMessage());
	}
	catch (Exception ex)
	{
		ex.printStackTrace();
		//System.out.println(ex.getMessage());
	}
  }

	////////////////////////////////// CVC ////////////////////////////////////

	// Modify these values for you test card!
	static String csCvcCertRole01 = "7F2181CD5F37818086B81455BCF0F508CF79FE9CAD574CA631EBC392D78869C4DC29DB193D75AC7E1BB1852AA57FA54C7E7FA97CBB536F2FA384C90C4FF62EAB119156016353AEAFD0F2E2B41BF89CCFE2C5F463A4A30DC38F2B9145DA3F12C40E2F394E7EE606A4C9377253D6E46D7B538B34C712B964F4A20A5724E0F6E88E0D5D1188C39B75A85F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for writing
	static String csCvcCertRole02 = "7F2181CD5F3781804823ED79D2F59E61E842ABE0A58919E63F362C9133E873CA77DD79AD01009247460DFE0294DD0ABAABE1D262E69A165F2F1AC6E953E8ABBE3BF1D2ACD6EB69EE83AB918D6F5116589BE0D40E780D5635238B78AA4290AD32F2A6316D24B417E06591DE6A775C38CFD918CA4FD11146EA20E06FE7F73CA7B3D3058FA259745D875F383C6917D61A07CFF92106D1885E393F68BA863520887168CE884242ED86F2F80397B42B883D931F8CCB141DC3579E5AB798B8CCF9A189B83B8D0001000142085054474F56101106"; // CVC cert for reading
	static String csCvcCertRole03 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3sRDCdqK8/wsapM8eWoNoLCVXCNG4wXnUcch2M5xgYjTvHeuX+w7REx7UiFPTSLjkl6fDPndFICqsxaxtxk4yEwoFQO7frMn4NdKPqRsN3TE+WDGD1plzwyExN6n8WRiuXz8yE4sLK2loLTrczjQ5cdwSBXsn/1P0jNy6fD5ggQIDAQAB";
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

		byte[] challenge = pteid.CVC_Init_SM101();
		byte[] signat = SignChallenge(challenge);
		pteid.CVC_Authenticate();

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

	private static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	public static String ToHex(byte[] ba)
	{
		StringBuffer buf = new StringBuffer(3 * ba.length + 2);
		for (int i = 0; i < ba.length; i++)
		{
			int c = ba[i];
			if (c < 0)
				c += 256;
			buf.append(HEX[c / 16]);
			buf.append(HEX[c % 16]);
			buf.append(' ');
		}

		return new String(buf);
	}

	private static byte[] makeBA(int len, byte val)
	{
		byte ret[] = new byte[len];
		for (int i = 0; i < len; i++)
			ret[i] = val;
		return ret;
	}

	/** Works only with when pteidlib is build with emulation code!!
	 */
	public void TestChangeAddress() throws Exception
	{
		System.out.println("\n*********************************************\n");

		// CVC_Init_SM101()
		byte[] ret = pteid.CVC_Init_SM101();
		System.out.println("CVC_Init_SM101: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// CVC_Authenticate_SM101
		byte[] signedChallenge = {0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
		byte[] ifdSerialNr = { 0x11, 0x22, 0x33, 0x44, 0x11, 0x22, 0x33, 0x44};
		byte[] iccSerialNr = { 0x44, 0x33, 0x22, 0x11	, 0x44, 0x33, 0x22, 0x11 };
		byte[] keyIfd= makeBA(32, (byte) 0x01);
		byte[] encKey = makeBA(16, (byte) 0x02);
		byte[] macKey = makeBA(16, (byte) 0x03);
		ret = pteid.CVC_Authenticate_SM101(signedChallenge,
			ifdSerialNr, iccSerialNr, keyIfd, encKey, macKey);
		System.out.println("CVC_Authenticate_SM101: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// CVC_R_Init()
		PTEID_DH_Params dhParams = pteid.CVC_R_Init();
		System.out.println("CVC_R_Init: G = " + ToHex(dhParams.G));
		System.out.println("CVC_R_Init: P = " + ToHex(dhParams.P));
		System.out.println("CVC_R_Init: Q = " + ToHex(dhParams.Q));

		System.out.println("\n*********************************************\n");

		// CVC_R_DH_Auth()
		byte[] Kidf = makeBA(8, (byte) 0x22);
		byte[] cvcCert = ToBytes(new BigInteger("00" + csCvcCertRole01, 16), 209);
		PTEID_DH_Auth_Response dhAuthResp = pteid.CVC_R_DH_Auth(Kidf, cvcCert);
		System.out.println("CVC_R_DH_Auth: Kicc = " + ToHex(dhAuthResp.Kicc));
		System.out.println("CVC_R_DH_Auth: challenge = " + ToHex(dhAuthResp.challenge));

		System.out.println("\n*********************************************\n");

		// CVC_R_ValidateSignature
		System.out.println("CVC_R_ValidateSignature: signedChallenge = " + ToHex(signedChallenge));
		pteid.CVC_R_ValidateSignature(signedChallenge);

		System.out.println("\n*********************************************\n");

		// SendAPDU()
		ret = pteid.SendAPDU(new byte[] {0x00, 0x20, 0x00, (byte) 0x81});
		System.out.println("Response to case 1 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { (byte)0x80, (byte)0x84, 0x00, 0x00, 0x08 });
		System.out.println("Response to case 2 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { 0x00, (byte)0xA4, 0x02, 0x0C, 0x02, 0x2F, 0x00 });
		System.out.println("Response to case 3 APDU: " + ToHex(ret));
		ret = pteid.SendAPDU(new byte[] { 0x00, (byte)0xA4, 0x02, 0x00, 0x02, 0x50, 0x31, 0x50 });
		System.out.println("Response to case 4 APDU: " + ToHex(ret));

		System.out.println("\n*********************************************\n");

		// ChangeAddress()
		byte[] serverCaCert = makeBA(1200, (byte) 0x05);
		PTEID_Proxy_Info proxyInfo = new PTEID_Proxy_Info();
		proxyInfo.proxy = "10.3.98.67";
		proxyInfo.port = 4444;
		proxyInfo.username = "userX";
		proxyInfo.password = "passwdX";
		pteid.ChangeAddress("https://www.test.com/ChangeAddress", serverCaCert,
			proxyInfo, "secretcode", "processcode");

		System.out.println("\n*********************************************\n");

		// GetChangeAddressProgress()
		int res = pteid.GetChangeAddressProgress();
		System.out.println("GetChangeAddressProgress(): returned " + res);

		System.out.println("\n*********************************************\n");

		// CancelChangeAddress()
		pteid.CancelChangeAddress();
		System.out.println("GetChangeAddressProgress(): done");

		System.out.println("\n*********************************************\n");

		// CAP_ChangeCapPin()
		pteid.CAP_ChangeCapPin("https://www.test.com/ChangeAddress", serverCaCert,
			proxyInfo, "1234", "123456");

		System.out.println("\n*********************************************\n");

		// CAP_GetCapPinChangeProgress
		res = pteid.CAP_GetCapPinChangeProgress();
		System.out.println("CAP_GetCapPinChangeProgress(): returned " + res);

		System.out.println("\n*********************************************\n");

		// CAP_CancelCapPinChange()
		pteid.CAP_CancelCapPinChange();
		System.out.println("CAP_CancelCapPinChange(): done");

		System.out.println("\n*********************************************\n");

		// GetLastWebErrorMessage()
		String msg = pteid.GetLastWebErrorMessage();
		System.out.println("GetLastWebErrorMessage(): returned " + msg);

		System.out.println("\n*********************************************\n");
	}
}