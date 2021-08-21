import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestCasesWithTxHandler extends TestCases {

	@Before
	public void setUpHandler() throws Exception {
		txHandler = new TxHandler(bitcoins.getPool());
	}

	@Test
	public void testMaxFeeTransferUNEQUAL() throws Exception {
		// Scrooge transfer 4 coins to Alice, 6 coins to bob, no transaction fee
		TransactionInterface tx1 = new Transaction();
		tx1 = authority.addSeller(tx1, bitcoins.getGenesiseTx(), 0);
		// tx1.addInput(bitcoins.getGenesiseTx().getHash(), 0);
		tx1 = authority.addBuyer(tx1,4,authority.getAlice());
		//tx1.addOutput(4, people.getAlice().getPublicKey());
		tx1 = authority.addBuyer(tx1,6,authority.getBob());
		//tx1.addOutput(6, authority.getBob().getPublicKey());
		tx1 = authority.authorizeSale(tx1,authority.getCreator(),0);
		// byte[] sig1 = authority.signMessage(authority.getCreator().getPrivateKey(), tx1.getRawDataToSign(0));
		// tx1.addSignature(sig1, 0);
		// tx1.finalize();

		// Alice transfer 3.4 to mike, transaction fee is 4-3.4=0.6
		Transaction tx2 = new Transaction();
		tx2.addInput(tx1.getHash(), 0);
		tx2.addOutput(3.4, authority.getMike().getPublicKey());
		byte[] sig = authority.signMessage(authority.getAlice().getPrivateKey(), tx2.getRawDataToSign(0));
		tx2.addSignature(sig, 0);
		tx2.finalize();

		// Bob transfer 5.5 to mike, transaction fee is 5-5.5=0.5
		Transaction tx3 = new Transaction();
		tx3.addInput(tx1.getHash(), 1);
		tx3.addOutput(5.5, authority.getMike().getPublicKey());
		sig = authority.signMessage(authority.getBob().getPrivateKey(), tx3.getRawDataToSign(0));
		tx3.addSignature(sig, 0);
		tx3.finalize();

		TransactionInterface[] acceptedRx = txHandler.handleTxs(new TransactionInterface[] { tx1, tx2, tx3 });
		assertEquals(acceptedRx.length, 3);
		assertFalse(Arrays.equals(acceptedRx[0].getHash(), tx2.getHash()));
	}
}
