package test;

//~--- JDK imports ------------------------------------------------------------

import java.io.*;
 
import java.net.URI;
 
import java.security.*;
import java.security.cert.*;
 
import java.util.*;
 
/**
 * Check the revocation status of a public key certificate using OCSP.
 */
public class ValidateCertUseOCSP{
 
   /*
    * Filename that contains the OCSP server's cert.
    */
   private static final String OCSP_SERVER_CERT = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
 
   /*
    * Filename that contains the root CA cert of the OCSP server's cert.
    */
   private static final String ROOT_CA_CERT = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
 
   /**
    * Checks the revocation status of a public key certificate using OCSP.
    *
    * We use the openssl calls to do this.
    */
   public static void main(String[] args) {
      try {
         CertPath cp               = null;
         Vector   certs            = new Vector();
         URI      ocspServer       = null;
         String   ocspServerString =
            "http://ocsp.auc.teste.cartaodecidadao.pt/publico/ocsp";
         String cert = "EC de Autenticacao do Cartao de Cidadao 0002.cer";
         // Try this with the openssl call
         Runtime rt      = Runtime.getRuntime();
         String  command = "openssl ocsp -issuer " + ROOT_CA_CERT
                           + " -CAfile  " + ROOT_CA_CERT
                           + " -cert "+ cert
                           + "-url " + ocspServerString;
         Process           proc   = rt.exec(command);
         InputStream       stderr = proc.getErrorStream();
         InputStreamReader isr    = new InputStreamReader(stderr);
         BufferedReader    br     = new BufferedReader(isr);
         String            line   = null;
         StringBuffer      sb  = new StringBuffer();
 
         sb.append("<ERROR>\n");
 
         while ((line = br.readLine()) != null) {
            sb.append(line);
         }
         sb.append("</ERROR>\n");
         line = sb.toString();
         if(!line.contains("Response verify OK")) {
            System.err.print(line);
         }
 
         InputStream       in  = proc.getInputStream();
         InputStreamReader inr = new InputStreamReader(in);
         BufferedReader    bin = new BufferedReader(inr);
 
         while ((line = bin.readLine()) != null) {
            System.out.println(line);
            sb.append(line);
         }
 
         int exitVal = proc.waitFor();
         System.out.println(exitVal);
         line = sb.toString();
 
         if (line.contains("good")) {
            System.out.println("Success");
         } else {
            System.out.println("Failure");
         }
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }
 
      System.exit(0);
   }
}