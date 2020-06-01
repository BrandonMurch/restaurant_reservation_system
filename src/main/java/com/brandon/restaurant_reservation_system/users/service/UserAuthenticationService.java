package com.brandon.restaurant_reservation_system.users.service;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/*
 * Creation and validation of passwords was created following the tutorial
 * by Lokesh Gupta 
 * 
 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash
 * -md5-sha-pbkdf2-bcrypt-examples/
 * 
 */

public class UserAuthenticationService {

    // Creates a password using the PBKDF2 algorithm
    public static String createPasswordHash(String input) {
	try {
	    return getHash(input);
	} catch (Exception e) {
	    System.out.println("Error, password creation failed");
	    return null;
	}
    }
    
    public static boolean validatePassword(String password, 
	    String storedPassword) {
	try {
	    return compareHash(password, storedPassword);
	} catch (UnsupportedEncodingException e) {
	    System.out.println("Error, password validation failed");
	    return false;
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    return false;
	} catch (InvalidKeySpecException e) {
	    e.printStackTrace();
	    return false;
	}
    }
    
    
    private static String getHash(String input) throws 
    NoSuchAlgorithmException, InvalidKeySpecException {
	byte[] salt = getSalt();
	return getHash(input, salt, 10000);
    }

    
    
    // generates a hash from the input, using the PBKDF2 algorithm.
    private static String getHash(String input, byte[] salt, int iterations) 
	    throws NoSuchAlgorithmException, InvalidKeySpecException {
	
	char[] chars = input.toCharArray();
	String algorithm = "PBKDF2WithHmacSHA1";
	
	PBEKeySpec keySpec = new PBEKeySpec(chars, salt, iterations, 64 * 8); 
	SecretKeyFactory secretKeyFactory = SecretKeyFactory
		.getInstance(algorithm);
	byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();
	Encoder encoder = Base64.getEncoder();
	String saltString = encoder.encodeToString(salt);
	String hashString = encoder.encodeToString(hash);
	return iterations + ":" + saltString + ":" + hashString;
    }
    
    
    // create a secure and random salt
    private static byte[] getSalt() throws NoSuchAlgorithmException {
	SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	byte[] salt = new byte[16];
	secureRandom.nextBytes(salt);
	return salt;
    }
    
    
    // Compare the inputted password to the stored password hash
    private static boolean compareHash(String password, String storedPassword) 
		    throws UnsupportedEncodingException, 
		    NoSuchAlgorithmException, InvalidKeySpecException {
	
	// stored hash "iterations:salt:hash"
	String[] parts = storedPassword.split(":");
	int iterations = Integer.parseInt(parts[0]);
	byte[] salt = Base64.getDecoder().decode(parts[1]);
	String passwordHash = getHash(password, salt, iterations);
	
	return storedPassword.equals(passwordHash);
    }
}
