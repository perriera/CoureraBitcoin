package com.coursera;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.*;

interface ICrypto {

    public void init(int key_size) throws Exception;

    public PublicKey genPublicKey();

    public PrivateKey genPrivateKey();
    public byte[] getMessage();
    public byte[] getSignature();

    public boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature);

};

public class Crypto implements ICrypto {

    PublicKey pubKey;
    PrivateKey prvKey;
    byte[] d, n, e, c, ep, eq, p, q;

    public Crypto() throws Exception {
        init(1024);
    }

    public PublicKey genPublicKey() {
        return pubKey;
    }

    public PrivateKey genPrivateKey() {
        return prvKey;
    }

    public byte[] getMessage() { return n;}
    public byte[] getSignature() { return d;}

    public void init(int key_size) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(key_size, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        pubKey = pair.getPublic();
        prvKey = pair.getPrivate();
        d = ((RSAPrivateKey) prvKey).getPrivateExponent().toByteArray();
        e = ((RSAPublicKey) pubKey).getPublicExponent().toByteArray();
        n = ((RSAPrivateKey) prvKey).getModulus().toByteArray();
        c = ((RSAPrivateCrtKey) prvKey).getCrtCoefficient().toByteArray();
        ep = ((RSAPrivateCrtKey) prvKey).getPrimeExponentP().toByteArray();
        eq = ((RSAPrivateCrtKey) prvKey).getPrimeExponentQ().toByteArray();
        p = ((RSAPrivateCrtKey) prvKey).getPrimeP().toByteArray();
        q = ((RSAPrivateCrtKey) prvKey).getPrimeQ().toByteArray();
    }

    /**
     * @return true is {@code signature} is a valid digital signature of
     *         {@code message} under the key {@code pubKey}. Internally, this uses
     *         RSA signature, but the student does not have to deal with any of the
     *         implementation details of the specific signature algorithm
     */
    public boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature) {
        Signature sig = null;
        try {
            sig = Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sig.initVerify(pubKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            sig.update(message);
            return sig.verify(signature);
        } catch (SignatureException e) {
            String msg = e.getMessage();
            e.printStackTrace();
        }
        return false;

    }
}
