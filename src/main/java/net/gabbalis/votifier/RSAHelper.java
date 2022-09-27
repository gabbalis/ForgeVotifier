package net.gabbalis.votifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final  Path conf = FileSystems.getDefault().getPath("config/.rsa");
    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;

    public RSAHelper(){
        if (loadKeyPair() != true){
            KeyPair pair = generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
            if (saveKeyPair() != true){
                LOGGER.warn("Failed to save the key pair. Public Key may be absent or invalid.");
            }
        }
    }
    private boolean loadKeyPair(){
        this.publicKey = null;
        this.privateKey = null;
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            String privateKeyStr = FileHelper.loadFile(conf, "id_rsa");
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
            String publicKeyStr = FileHelper.loadFile(conf, "id_rsa.pub");
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));
        }
        catch(FileNotFoundException e){
            LOGGER.info("RSA file not found. A new file may need to be generated.");
            return false;
        }
        catch(NoSuchAlgorithmException e){
            LOGGER.error("No such algorithm exception loading RSA. This error should be unreachable.");
            return false;
        }
        catch (InvalidKeySpecException e){
            LOGGER.error("InvalidKeySpecException: " + e.getMessage());
            return false;
        }

        return (this.publicKey!=null && this.privateKey !=null);
    }
    private static KeyPair generateKeyPair(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            return pair;
        }
        catch(NoSuchAlgorithmException e){
            LOGGER.error("No such algorithm exception loading RSA. This error should be unreachable.");
            return null;
        }
        //PrivateKey privateKey = pair.getPrivate();
        //PublicKey publicKey = pair.getPublic();
    }
    private boolean saveKeyPair(){
        //returns true if the save is successful
        if (privateKey == null || publicKey == null){
            LOGGER.error("Could not save Key Pair because keys were not initialized. This code should not be reachable.");
            return false;
        }
        Path conf = FileSystems.getDefault().getPath("config/.rsa");
        FileHelper.saveFile(conf, "id_rsa", Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        FileHelper.saveFile(conf, "id_rsa.pub", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        return true;
    }

    public byte[] encrypt(byte[] bData, PublicKey key){
        byte[] oData = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            oData = cipher.doFinal(bData);
        }
        catch(Exception e){
            LOGGER.error("encryption failed with: " + e.getMessage());
        }
        return oData;
    }
    public byte[] decrypt(byte[] bData, PrivateKey key){
        byte[] oData = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            oData = cipher.doFinal(bData);

        }
        catch(Exception e){
            LOGGER.error("decryption failed with: " + e.getMessage());
        }
        return oData;
    }

    public PrivateKey getPrivateKey() {
        if (this.privateKey == null){
            LOGGER.error("getPrivateKey called in bad state: key is null");
        }
        return this.privateKey;
    }
    public PublicKey getPublicKey() {
        if (this.publicKey == null){
            LOGGER.error("getPrivateKey called in bad state: key is null");
        }
        return this.publicKey;
    }
}
