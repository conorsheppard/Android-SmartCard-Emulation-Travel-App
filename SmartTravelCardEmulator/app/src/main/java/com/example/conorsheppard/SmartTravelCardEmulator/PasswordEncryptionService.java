package com.example.conorsheppard.SmartTravelCardEmulator;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import android.util.Base64;

import org.spongycastle.openssl.PEMReader;


public class PasswordEncryptionService {

    private String publicKey;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public PasswordEncryptionService() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        this.publicKey = this.getPublicKey();
    }

    // *** Function to encrypt the data. *** //
    public String encrypt( String data ) throws Exception {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
        byte[] keyBytes = Base64.decode(this.publicKey, 0);
        PublicKey publickey = strToPublicKey(new String(keyBytes));
        byte[] encryptedBytes = null;
        if(publickey != null) {
            cipher.init(Cipher.ENCRYPT_MODE , publickey);
            // Base 64 encode the encrypted data
            encryptedBytes = Base64.encode(cipher.doFinal(data.getBytes()), 0);
        } else {
            throw new RuntimeException("Failed to create public key");
        }

        return new String(encryptedBytes);
    }


    public static PublicKey strToPublicKey(String s) {
        PublicKey pbKey = null;
        try {
            BufferedReader br = new BufferedReader( new StringReader(s) );
            PEMReader pr = new PEMReader(br);
            Object obj = pr.readObject();
            if( obj instanceof PublicKey ) {
                pbKey = (PublicKey) obj;
            } else if( obj instanceof KeyPair ) {
                KeyPair kp = (KeyPair) obj;
                pbKey = kp.getPublic();
            } else if(PublicKey.class.isAssignableFrom(obj.getClass())) {
                pbKey = (PublicKey) obj;
            } else if(KeyPair.class.isAssignableFrom(obj.getClass())) {
                KeyPair kp = (KeyPair) obj;
                pbKey = kp.getPublic();
            }
            pr.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return pbKey;
    }


    private String getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        // Base 64 encoded public key
        return "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0NCk1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRRE1PZGtaN28yMmJiSTAxZnY5QWtvMXFmTHd6MzRBYlA3ejgrcUMNCi9XZGNHdEFmdGk1QUFvZWUxOTV2NUd1QTZhak9JS3Y1cDkzdEdFNitWamdRdG03d0phS25kMno1UGpvZHIvc2R5bW9hNGdZT2dXcHQNCmVvamRybFZmeHZBbUxUN0JsWEExNGNuQUxDc3prYUtmTktSeEIrWDZlZGIyQkVaVnlKYThQcTBLYndJREFRQUINCi0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQ==";
    }
}
