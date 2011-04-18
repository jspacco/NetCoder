package edu.ycp.cs.netcoder.server.problems;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

/**
 * Password hashing utility methods.
 */
public abstract class HashPassword {
	/**
	 * Compute the hex encoded hash of given plaintext password,
	 * using given hex encoded salt value.
	 * Uses MD-5 algorithm, so resulting string will
	 * be 32 characters long (encoding a 16 byte hash).
	 * 
	 * @param plaintextPassword a plaintext password
	 * @param salt              a hex encoded salt value
	 * @return  hex encoded password hash
	 */
	public static String computeHash(String plaintextPassword, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			md.update(hexStringToByteArray(salt));
			md.update(plaintextPassword.getBytes(Charset.forName("UTF-8")));
			
			byte[] hash = md.digest();
			
			return byteArrayToHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Cannot find MD5 algorithm?", e);
		}
	}
	
	private static final String HEX = "0123456789abcdef";
	
	private static String byteArrayToHexString(byte[] byteArray) {
		StringBuilder buf = new StringBuilder();
		for (byte b : byteArray) {
			buf.append(HEX.charAt((b >>> 4) & 0xF));
			buf.append(HEX.charAt(b & 0xF));
		}
		return buf.toString();
	}
	
	private static byte[] hexStringToByteArray(String s) {
		if (s.length() % 2 != 0) {
			throw new IllegalArgumentException("Invalid hex string: " + s);
		}
		byte[] result = new byte[s.length() / 2];
		for (int i = 0; i < s.length(); i += 2) {
			char c = s.charAt(i);
			char c2 = s.charAt(i + 1);
			
			byte b = hexValue(c);
			b <<= 4;
			b += hexValue(c2);
			result[i/2] = b;
		}
		return result;
	}
	
	public static String generateRandomSalt(Random r) {
		byte[] saltBytes = new byte[8];
		r.nextBytes(saltBytes);
		return byteArrayToHexString(saltBytes);
	}

	private static byte hexValue(char c) {
		if (c >= '0' && c <= '9') {
			return (byte) (c - '0');
		} else if (c >= 'a' && c <= 'f') {
			return (byte) ((c - 'a') + 10);
		} else if (c >= 'A' && c <= 'F') {
			return (byte) ((c - 'A') + 10);
		} else {
			throw new IllegalArgumentException("Invalid hex character: " + c);
		}
	}
	
	public static void main(String[] args) {
		 Scanner keyboard = new Scanner(System.in);
		 System.out.print("Enter plaintext password: ");
		 String plaintextPassword = keyboard.nextLine();
		 
		 String salt = generateRandomSalt(new Random());
		 System.out.println("Salt is " + salt);
		 String hashPasswd = computeHash(plaintextPassword, salt);
		 System.out.println("Hashed password is " + hashPasswd);
	}
}
