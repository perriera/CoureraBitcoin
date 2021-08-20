
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

abstract public class BasicHandlerTests {
	protected BitcoinPeople people;
	protected BitCoinPool bitcoins;
	protected TxHandlerInterface txHandler;

	abstract public void setUp() throws Exception;

	@Test(expected = VerifySignatureOfConsumeCoinException.class)
	public void testValidTxSign() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.getAlice().getPublic());
		byte[] sig1 = people.signMessage(people.getAlice().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		txHandler.isValidTx(tx1);
	}

	@Test
	public void testValidTxSign2() throws Exception {
		Transaction tx2 = new Transaction();
		tx2.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx2.addOutput(10, people.getAlice().getPublic());
		byte[] sig2 = people.signMessage(people.getScrooge().getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();
		assertTrue(txHandler.isValidTx(tx2));

		Transaction tx3 = new Transaction();
		tx3.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx3.addOutput(4, people.getAlice().getPublic());
		tx3.addOutput(6, people.getBob().getPublic());
		byte[] sig3 = people.signMessage(people.getScrooge().getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		assertTrue(txHandler.isValidTx(tx3));
	}

	@Test(expected = TransactionInputSumLessThanOutputSumException.class)
	public void testValidTxValue() throws Exception {
		Transaction tx = new Transaction();
		tx.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx.addOutput(4, people.getAlice().getPublic());
		tx.addOutput(7, people.getBob().getPublic());
		byte[] sig = people.signMessage(people.getScrooge().getPrivate(), tx.getRawDataToSign(0));
		tx.addSignature(sig, 0);
		tx.finalize();
		txHandler.isValidTx(tx);
	}

	@Test(expected = TransactionOutputLessThanZeroException.class)
	public void testValidTxValue2() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(4, people.getAlice().getPublic());
		tx1.addOutput(-7, people.getBob().getPublic());
		byte[] sig1 = people.signMessage(people.getScrooge().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		txHandler.isValidTx(tx1);
	}

	@Test
	public void testTransfer() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.getAlice().getPublic());
		byte[] sig1 = people.signMessage(people.getScrooge().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		assertTrue(txHandler.isValidTx(tx1));
		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1 });
		assertEquals(acceptedRx.length, 1);

		// Alice transfer 4 to bob, 6 to mike
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(4, people.getBob().getPublic());
		tx2.addOutput(6, people.getMike().getPublic());
		byte[] sig2 = people.signMessage(people.getAlice().getPrivate(), tx2.getRawDataToSign(0));
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
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.getAlice().getPublic());
		byte[] sig1 = people.signMessage(people.getScrooge().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		// Alice transfer 4 to bob, 6 to mike
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(4, people.getBob().getPublic());
		tx2.addOutput(6, people.getMike().getPublic());
		byte[] sig2 = people.signMessage(people.getAlice().getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();

		// Bob transfer 4 to mike
		Transaction tx3 = new Transaction();
		tx3.addInput(tx2.getHash(), 0);
		tx3.addOutput(4, people.getMike().getPublic());
		byte[] sig3 = people.signMessage(people.getBob().getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();

		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1, tx2, tx3 });
		assertEquals(acceptedRx.length, 3);
	}

	@Test
	public void testDoubleSpending() throws Exception {
		// Scrooge transfer 10 coins to Alice
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.getAlice().getPublic());
		byte[] sig1 = people.signMessage(people.getScrooge().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();

		assertTrue(txHandler.isValidTx(tx1));
		Transaction[] acceptedRx = txHandler.handleTxs(new Transaction[] { tx1 });
		assertEquals(acceptedRx.length, 1);

		// Alice transfer 10 coins to bob
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(10, people.getBob().getPublic());
		byte[] sig2 = people.signMessage(people.getAlice().getPrivate(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig2, 0);
		tx2.finalize();
		assertTrue(txHandler.isValidTx(tx2));
		acceptedRx = txHandler.handleTxs(new Transaction[] { tx2 });
		assertEquals(acceptedRx.length, 1);

	}

	@Test(expected = ConsumedCoinAvailableException.class)
	public void testDoubleSpending2() throws Exception {
		Transaction tx1 = new Transaction();
		tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1.addOutput(10, people.getAlice().getPublic());
		byte[] sig1 = people.signMessage(people.getScrooge().getPrivate(), tx1.getRawDataToSign(0));
		tx1.addSignature(sig1, 0);
		tx1.finalize();
		// Alice then transfer the same 10 coins to mike
		Transaction tx3 = new Transaction();
		tx3.addInput(tx1.getHash(), 0);
		tx3.addOutput(10, people.getBob().getPublic());
		byte[] sig3 = people.signMessage(people.getAlice().getPrivate(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig3, 0);
		tx3.finalize();
		txHandler.isValidTx(tx3);
	}

}


