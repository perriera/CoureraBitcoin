
package com.coursera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CryptoTest {
    private static ICrypto crypto;

    @BeforeClass
    public static void initCrypto() throws Exception{
        crypto = new Crypto();
    }

    @Before
    public void beforeEachTest() {
        System.out.println("This is executed before each Test");
    }

    @After
    public void afterEachTest() {
        System.out.println("This is exceuted after each Test");
    }

    @Test
    public void testSignature() {
        try {
            crypto.init(1024);
            PublicKey pubKey = crypto.genPublicKey();
            byte[] message = crypto.getMessage();
            byte[] signature = crypto.getSignature();
            boolean result = crypto.verifySignature(pubKey, message, signature);
            assertTrue("It works", !result);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            System.out.println(msg);
        }

        // assertEquals(7, result);
    }

}
