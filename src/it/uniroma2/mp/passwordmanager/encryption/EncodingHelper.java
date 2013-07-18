package it.uniroma2.mp.passwordmanager.encryption;

/***
 * Classe utilizzata per effettuare l'encoding di stringhe
 * **/

public class EncodingHelper {
	
	private final static String HEX = "0123456789ABCDEF";
	
	/***
	 * @param txt testo di input
	 * @return stringa convertita in esadecimale.
	 * **/
    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    
    /***
	 * @param hex stringa esadecimale di input
	 * @return string di byte
	 * **/
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    /***
	 * @param hexString stringa esadecimale di input
	 * @return la stringa converitita in un array di byte.
	 * **/
    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }
    
    /***
	 * @param buf array di byte di input
	 * @return resituisce l'array di byte converito in una stringa esadecimale
	 * **/
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    
    /***
	 * Prende il byte b e lo concatena alla StringBuffer sb
	 * @return void
	 * **/
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
