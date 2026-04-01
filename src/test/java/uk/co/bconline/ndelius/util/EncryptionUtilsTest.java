package uk.co.bconline.ndelius.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionUtilsTest {

    @Test
    public void testEncryption() {
        String plainText = "Smith, John";
        String secretKey = "ThisIsASecretKey";

        String encrypted = EncryptionUtils.encrypt(plainText, secretKey);

        assertThat(encrypted).isEqualTo("i8/p1Ti7JMS/jO+POhHtGA==");
    }

    @Test
    public void testEncryptionAndDecryption() {
        String plainText = "Some Plain Text";
        String secretKey = "ThisIsASecretKey";

        String encrypted = EncryptionUtils.encrypt(plainText, secretKey);
        String decrypted = EncryptionUtils.decrypt(encrypted, secretKey);

        assertThat(decrypted).isEqualTo(plainText);
        assertThat(encrypted).isNotEqualTo(plainText);
    }
}
