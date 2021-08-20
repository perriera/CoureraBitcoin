import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import org.junit.Before;
import org.junit.Test;

public class TxHandlerTest {
	private SampleBitcoinPeople people;
	private Transaction genesiseTx;
	private TxHandler txHandler;

	@Before
	public void setUp() throws Exception {
		people = new SampleBitcoinPeople();
		GenerateInitialCoins();
	}

	@Test(expected = VerifySignatureOfConsumeCoinException.class)
	public void testValidTxSign() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.aliceKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		txHandler.isValidTx(tx1);
	}

	@Test
	public void testValidTxSign2() throws Exception {
		Transaction tx2 = new Transaction();
		tx2.addInput(genesiseTx.getHash(), 0);
		tx2.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig2 = signMessage(people.scroogeKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();
		assertTrue(txHandler.isValidTx(tx2));

		Transaction tx3 = new Transaction();
		tx3.addInput(genesiseTx.getHash(), 0);
		tx3.addOutput(4, people.aliceKeypair.getPublic());
		tx3.addOutput(6, people.bobKeypair.getPublic());
		byte[] sig3 = signMessage(people.scroogeKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		assertTrue(txHandler.isValidTx(tx3));
	}

	@Test(expected = TransactionInputSumLessThanOutputSumException.class)
	public void testValidTxValue() throws Exception {
		Transaction tx = new Transaction();
		tx.addInput(genesiseTx.getHash(), 0);
		tx.addOutput(4, people.aliceKeypair.getPublic());
		tx.addOutput(7, people.bobKeypair.getPublic());
		byte[] sig = signMessage(people.scroogeKeypair.getPrivate(), tx.getRawDataToSign(0));
		tx.addSignature(sig, 0);
		tx.finalize();
		txHandler.isValidTx(tx);
	}

	@Test(expected = TransactionOutputLessThanZeroException.class)
	public void testValidTxValue2() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
		tx1.addOutput(4, people.aliceKeypair.getPublic());
		tx1.addOutput(-7, people.bobKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		txHandler.isValidTx(tx1);
	}

	@Test
	public void testTransfer() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		assertTrue(txHandler.isValidTx(tx1));
		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1 });
		assertEquals(acceptedRx.length, 1);

		// Alice transfer 4 to bob, 6 to mike
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(4, people.bobKeypair.getPublic());
		tx2.addOutput(6, people.mikeKeypair.getPublic());
		byte[] sig2 = signMessage(people.aliceKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();

		assertTrue(txHandler.isValidTx(tx2));
		acceptedRx = txHandler.handleTxs(new Transaction[] { tx2 });
		assertEquals(acceptedRx.length, 1);
	}

	@Test
	public void testMultipTxDepenonEachother() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		// Alice transfer 4 to bob, 6 to mike
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(4, people.bobKeypair.getPublic());
		tx2.addOutput(6, people.mikeKeypair.getPublic());
		byte[] sig2 = signMessage(people.aliceKeypair.getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();

		// Bob transfer 4 to mike
		Transaction tx3 = new Transaction();
		tx3.addInput(tx2.getHash(), 0);
		tx3.addOutput(4, people.mikeKeypair.getPublic());
		byte[] sig3 = signMessage(people.bobKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();

		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1, tx2, tx3 });
		assertEquals(acceptedRx.length, 3);
	}

	@Test
	public void testDoubleSpending() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
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

	}

	@Test(expected = ConsumedCoinAvailableException.class)
	public void testDoubleSpending2() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(genesiseTx.getHash(), 0);
		tx1.addOutput(10, people.aliceKeypair.getPublic());
		byte[] sig1 = signMessage(people.scroogeKeypair.getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		// Alice then transfer the same 10 coins to mike
		Transaction tx3 = new Transaction();
		tx3.addInput(tx1.getHash(), 0);
		tx3.addOutput(10, people.bobKeypair.getPublic());
		byte[] sig3 = signMessage(people.aliceKeypair.getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		txHandler.isValidTx(tx3);
	}

	private void GenerateInitialCoins() {
		genesiseTx = new Transaction();
		genesiseTx.addOutput(10, people.scroogeKeypair.getPublic());
		genesiseTx.finalize();

		UTXOPool pool = new UTXOPool();
		UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
		pool.addUTXO(utxo, genesiseTx.getOutput(0));

		txHandler = new TxHandler(pool);
	}

	private byte[] signMessage(PrivateKey sk, byte[] message)
			throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initSign(sk);
		sig.update(message);
		return sig.sign();
	}

}
