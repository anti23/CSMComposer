
import java.io.*;
import java.security.MessageDigest;


/*
 * Geklaut von http://www.rgagnon.com/javadetails/java-0416.html
 */
public class MD5ChecksumGenerator {
	
	static final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};    


   public static byte[] createChecksum(String filename) throws
       Exception
   {
     InputStream fis =  new FileInputStream(filename);

     byte[] buffer = new byte[1024];
     MessageDigest complete = MessageDigest.getInstance("MD5");
     int numRead;
     do {
      numRead = fis.read(buffer);
      if (numRead > 0) {
        complete.update(buffer, 0, numRead);
        }
      } while (numRead != -1);
     fis.close();
     return complete.digest();
   }


   public static String getHexStringFast(byte[] raw) 
     throws UnsupportedEncodingException 
   {
     byte[] hex = new byte[2 * raw.length];
     int index = 0;

     for (byte b : raw) {
       int v = b & 0xFF;
       hex[index++] = HEX_CHAR_TABLE[v >>> 4];
       hex[index++] = HEX_CHAR_TABLE[v & 0xF];
     }
     return new String(hex, "ASCII");
   }
   
   public static String getHexStringSlow(byte[] raw) 
   throws UnsupportedEncodingException 
   {
	   String result = "";
	   for (int i=0; i < raw.length; i++) {
		   result +=
			   Integer.toString( ( raw[i] & 0xff ) + 0x100, 16).substring( 1 );
	   }
	   return result;
   }

   public static String getMD5Checksum(String filename) throws Exception {
     byte[] b = createChecksum(filename);
     return getHexStringFast(b);
   }

   public static void main(String args[]) {

	   byte[] data= {(byte)1,(byte)1,(byte)1,(byte)1};
	   
	   File f = new File("");
     try {
       System.out.println(getHexStringSlow(createChecksum("Trial003.csm")));
       System.out.println(getHexStringFast(createChecksum("Trial003.csm")));
	     }
	     catch (Exception e) {
	         e.printStackTrace();
	     }
	   }
 
}
