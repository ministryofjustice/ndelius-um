package uk.co.bconline.ndelius.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionUtilsTest {

	@Test
	public void testEncryption() {
		String plainText = "Smith, John";
		String secretKey = "ThisIsASecretKey";

		String encrypted = EncryptionUtils.encrypt(plainText, secretKey);

		assertEquals("i8/p1Ti7JMS/jO+POhHtGA==", encrypted);
	}

	@Test
	public void testEncryptionAndDecryption() {
		String plainText = "Some Plain Text";
		String secretKey = "ThisIsASecretKey";

		String encrypted = EncryptionUtils.encrypt(plainText, secretKey);
		String decrypted = EncryptionUtils.decrypt(encrypted, secretKey);

		assertEquals(plainText, decrypted);
		assertNotEquals(plainText, encrypted);
	}
}