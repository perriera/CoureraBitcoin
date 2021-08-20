import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class MaxFeeTxHandlerTest {
	private SampleBitcoinPeople people;
	private BitCoinsFor bitcoins;
	private TxHandlerInterface txHandler;

	@Before
	public void setUp() throws Exception {
		people = new SampleBitcoinPeople();
		bitcoins = new BitCoinsFor(people);
		txHandler = new MaxFeeTxHandler(bitcoins.getPool());
	}

	@Test(expected = VerifySignatureOfConsumeCoinException.class)
	public void testValidTxSign() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.aliceKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		txHandler.isValidTx(tx1);
	}

	@Test
	public void testValidTxSign2() throws Exception {
		Transaction tx2 = new Transaction();
		tx2.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx2.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig2 = signMessage(people.scroogeKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();
		assertTrue(txHandler.isValidTx(tx2));

		Transaction tx3 = new Transaction();
		tx3.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx3.addOutput(4, people.aliceKeypair.getPublic());
		tx3.addOutput(6, people.bobKeypair.getPublic());
		byte[] sig3 = signMessage(people.scroogeKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		assertTrue(txHandler.isValidTx(tx3));
	}

	@Test
	public void testValidTxValue() throws Exception {
		Transaction tx = new Transaction();
		tx.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx.addOutput(4, people.aliceKeypair.getPublic());
		tx.addOutput(7, people.bobKeypair.getPublic());
		byte[] sig = signMessage(people.scroogeKeypair.getPrivate(), tx.getRawDataToSign(0));
		tx.addSignature(sig, 0);
		tx.finalize();
		assertFalse(txHandler.isValidTx(tx));

		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(4, people.aliceKeypair.getPublic());
		tx1.addOutput(-7, people.bobKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx.finalize();
		assertFalse(txHandler.isValidTx(tx1));
	}

	@Test
	public void testMaxFeeTransfer() throws Exception {
		// Scrooge transfer 4 coins to Alice, 6 coins to bob, no transaction fee
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(4, people.aliceKeypair.getPublic());
		tx1.addOutput(6, people.bobKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		// Alice transfer 3.4 to mike, transaction fee is 4-3.4=0.6
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(3.4, people.mikeKeypair.getPublic());
		byte[] sig = signMessage(people.aliceKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig, 0);
		tx2.finalize();

		// Bob transfer 5.5 to mike, transaction fee is 5-5.5=0.5
		Transaction tx3 = new Transaction();
		tx3.addInput(tx1.getHash(), 1);
		tx3.addOutput(5.5, people.mikeKeypair.getPublic());
		sig = signMessage(people.bobKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig, 0);
		tx3.finalize();

		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1, tx2, tx3 });
		assertEquals(acceptedRx.length, 3);
		assertTrue(Arrays.equals(acceptedRx[0].getHash(), tx2.getHash()));
	}

	@Test(expected = ConsumedCoinAvailableException.class)
	public void testDoubleSpending() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		assertTrue(txHandler.isValidTx(tx1));
		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1 });
		assertEquals(acceptedRx.length, 1);

		// Alice transfer 10 coins to bob
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(10, people.bobKeypair.getPublic());
		byte[] sig2 = signMessage(people.aliceKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();
		assertTrue(txHandler.isValidTx(tx2));
		acceptedRx = txHandler.handleTxs(new Transaction[] { tx2 });
		assertEquals(acceptedRx.length, 1);

		// Alice then transfer the same 10 coins to mike
		Transaction tx3 = new Transaction();
		tx3.addInput(tx1.getHash(), 0);
		tx3.addOutput(10, people.bobKeypair.getPublic());
		byte[] sig3 = signMessage(people.aliceKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		assertFalse(txHandler.isValidTx(tx3));
	}

	private byte[] signMessage(PrivateKey sk, byte[] message)
			throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initSign(sk);
		sig.update(message);
		return sig.sign();
	}

}
