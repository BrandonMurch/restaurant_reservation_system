package com.brandon.restaurant_reservation_system.users.service;


import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Base64.Encoder;

/*
 * Creation and validation of passwords was modified from a tutorial
 * by Lokesh Gupta
 *
 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash
 * -md5-sha-pbkdf2-bcrypt-examples/
 *
 */

public class UserPasswordEncoder implements PasswordEncoder {

	private final int ITERATIONS = 64000;

	// Creates a password using the PBKDF2 algorithm
	@Override
	public String encode(CharSequence rawPassword) {
		try {
			byte[] salt = createSalt();
			return createHash(rawPassword, salt, ITERATIONS);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword,
						   String storedPassword) {
		// stored hash "iterations:salt:hash"
		String[] parts = storedPassword.split(":");
		int iterations = Integer.parseInt(parts[0]);
		byte[] salt = Base64.getDecoder().decode(parts[1]);
		String passwordHash = null;
		try {
			passwordHash = createHash(rawPassword, salt, iterations);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return storedPassword.equals(passwordHash);
	}


	@Override
	public boolean upgradeEncoding(String hash) {
		String[] parts = hash.split(":");
		int iterations = Integer.parseInt(parts[0]);
		return this.ITERATIONS == iterations;
	}

	// generates a hash from the input, using the PBKDF2 algorithm.
	private String createHash(CharSequence rawPassword, byte[] salt, int iterations)
	throws NoSuchAlgorithmException, InvalidKeySpecException {

		char[] chars = sequenceToArray(rawPassword);
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

	private char[] sequenceToArray(CharSequence sequence) {
		int length = sequence.length();
		char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = sequence.charAt(i);
		}
		return result;
	}

	// create a secure and random salt
	private byte[] createSalt() throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		secureRandom.nextBytes(salt);
		return salt;
	}
}
