package uk.co.bconline.ndelius.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EncryptionUtils
{

	public static String encrypt(String plainText, String secret)
	{
		try
		{
			final Cipher cipher = cipherFromSecret(Cipher.ENCRYPT_MODE, secret);
			return cipher != null ? new String(Base64.encodeBase64(cipher.doFinal(plainText.getBytes()), true)) : null;
		}
		catch (IllegalBlockSizeException | BadPaddingException ex)
		{
			return null;
		}
	}

	public static String decrypt(String encrypted, String secret)
	{
		try
		{
			final Cipher cipher = cipherFromSecret(Cipher.DECRYPT_MODE, secret);
			return cipher != null ? new String(cipher.doFinal(Base64.decodeBase64(Arrays.toString(encrypted.getBytes())))) : null;
		}
		catch (IllegalBlockSizeException | BadPaddingException ex)
		{
			return null;
		}
	}

	private static Cipher cipherFromSecret(int initMode, String secret)
	{
		try
		{
			final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(initMode, keyFromSecret(secret));
			return cipher;
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex)
		{
			return null;
		}
	}

	private static Key keyFromSecret(String secret)
	{
		try
		{
			byte[] digest = MessageDigest.getInstance("SHA-1").digest(secret.getBytes());
			return new SecretKeySpec(Arrays.copyOf(digest, 16), "AES");
		}
		catch (NoSuchAlgorithmException ex)
		{
			return null;
		}
	}
}
