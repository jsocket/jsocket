package net.jsocket;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Contains encrypted {@link DataCarrier DataCarrier} and encryption IV when it is transported
 */
public class EncryptedCarrier implements Serializable {
    private static final String transformation = "AES/CBC/PKCS5Padding";
    private byte[] encrypted;
    private byte[] IV;

    /**
     * Initialises the EncryptedMessage and encrypts the dataCarrier
     *
     * @param dataCarrier  The data to be encrypted and stored
     * @param symmetricKey The symmetric key used by both the client and the server
     * @throws BadPaddingException Thrown when an incorrect (as in wrong length/format) key is passed as the symmetricKey
     */
    public EncryptedCarrier(DataCarrier dataCarrier, SecretKey symmetricKey) throws BadPaddingException {
        IV = new byte[128 / 8];
        new SecureRandom().nextBytes(IV);
        try {
            Cipher ci = Cipher.getInstance(transformation);
            ci.init(Cipher.ENCRYPT_MODE, symmetricKey, new IvParameterSpec(IV));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(dataCarrier);
            out.flush();
            byte[] clear = bos.toByteArray();
            encrypted = ci.doFinal(clear);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            System.out.println("This exception should not have happened...");
        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the data in this message and returns it
     *
     * @param symmetricKey The symmetric key used by both the client and the server
     * @return The decrypted DataCarrier containing the actual message metadata and payload
     * @throws BadPaddingException Thrown when an incorrect (as in wrong length/format) key is passed as the symmetricKey
     * @throws InvalidKeyException Thrown when an incorrect (as in wrong data) key is passed as the symmetricKey
     */
    public DataCarrier getDataCarrier(SecretKey symmetricKey) throws BadPaddingException, InvalidKeyException {
        try {
            Cipher ci = Cipher.getInstance(transformation);
            ci.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(IV));
            ByteArrayInputStream bis = new ByteArrayInputStream(ci.doFinal(encrypted));
            ObjectInputStream in = new ObjectInputStream(bis);
            return (DataCarrier) in.readObject();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            System.out.println("This exception should not have happened...");
        } catch (ClassNotFoundException | InvalidAlgorithmParameterException | IllegalBlockSizeException | IOException e) {
            //TODO Exception handling
            e.printStackTrace();
        }
        return null;
    }
}
