package it.uniroma2.mp.passwordmanager.encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;


public class HashHelper {
	
	private static String convertToHex(byte[] data) throws java.io.IOException 
	{
		StringBuffer sb = new StringBuffer();
		String hex=null;

		hex=Base64.encodeToString(data, 0, data.length, 0);

		sb.append(hex);

		return sb.toString();
	}

	public static String computeSHAHash(String password)
	{
		MessageDigest mdSha1 = null;
		try
		{
			mdSha1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			mdSha1.update(password.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] data = mdSha1.digest();
		
		String SHAHash = "";
		
		try {
			SHAHash = convertToHex(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return SHAHash;
	}
}
