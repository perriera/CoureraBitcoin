import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class TestCasesFormal {

    private ScroogeCoinAuthority authority;
    private TransactionInterface genesiseTx;
    private UTXOPool pool;

    @Before
    public void setUpHandler() throws Exception {
        authority = new ScroogeCoinAuthority();
        genesiseTx = authority.getCreator().createCoin(10);
        pool = new UTXOPool();
        UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
        pool.addUTXO(utxo, genesiseTx.getOutput(0));
    }

    /**
     * Every transaction has a set of inputs and a set of outputs. An input in a
     * transaction must use a hash pointer to refer to its corresponding output in
     * the previous transaction, and it must be signed with the private key of the
     * owner because the owner needs to prove he/she agrees to spend his/her coins.
     * Every output is correlated to the public key of the receiver, which is
     * his/her ScroogeCoin address.
     * 
     * @throws Exception
     */
    @Test
    public void testTransfer() throws Exception {

        /**
         * @brief Scrooge has right to create coins
         * 
         *        In the first transaction, we assume that Scrooge has created 10 coins
         *        and assigned them to himself, we don’t doubt that because the
         *        system-Scroogecoin has a building rule which says that Scrooge has
         *        right to create coins.
         * 
         */

        // Correction: Scrooge transfer 4 coins to Alice, 6 coins to bob, no transaction fee

        TransactionInterface tx1 = new Transaction();
        tx1 = authority.addCoinForSale(tx1, genesiseTx, 0);
        tx1 = authority.addBuyer(tx1, 4, authority.getAlice());
        tx1 = authority.addBuyer(tx1, 6, authority.getBob());
        tx1 = authority.authorizeSale(tx1, authority.getCreator(), 0);

        /**
         * In the second transaction, Scrooge transferred 3.9 coins to Alice and 5.9
         * coins to Bob. The sum of the two outputs is 0.2 less than the input because
         * the transaction fee was 0.2 coin.
         */

        // Correction: Alice transfer 3.4 to mike, transaction fee is 4-3.4=0.6

        TransactionInterface tx2 = new Transaction();
        tx2 = authority.addCoinForSale(tx2, tx1, 0);
        tx2 = authority.addBuyer(tx2, 3.4, authority.getMike());
        tx2 = authority.authorizeSale(tx2, authority.getAlice(), 0);

        /**
         * In the third transaction, there were two inputs and one output, Alice and Bob
         * transferred 9.7 coins to mike, and the transaction fee was 0.1 coin.
         */

        // Correction: Bob transfer 5.5 to mike, transaction fee is 5-5.5=0.5

        TransactionInterface tx3 = new Transaction();
        tx3 = authority.addCoinForSale(tx3, tx1, 1);
        tx3 = authority.addBuyer(tx3, 5.5, authority.getMike());
        tx3 = authority.authorizeSale(tx3, authority.getBob(), 0);

        /**
         * Unclaimed transaction outputs pool 
         * 
         * Another trick we need to note when doing
         * the programming assignment is that an UTXOPool is introduced to track the
         * unclaimed outputs (unspent coins), so we can know whether the corresponding
         * output of an input of the transaction is available or not.
         * 
         */

        TxHandlerInterface maxFeeTxHandler = new MaxFeeTxHandler(pool);
        TransactionInterface[] maxAcceptedRx = maxFeeTxHandler.handleTxs(new TransactionInterface[] { tx1, tx2, tx3 });
        assertEquals(maxAcceptedRx.length, 3);
        assertTrue(Arrays.equals(maxAcceptedRx[0].getHash(), tx2.getHash()));

        TxHandlerInterface txHandler = new TxHandler(pool);
        TransactionInterface[] txAcceptedRx = txHandler.handleTxs(new TransactionInterface[] { tx1, tx2, tx3 });
        assertEquals(txAcceptedRx.length, 3);
        assertFalse(Arrays.equals(txAcceptedRx[0].getHash(), tx2.getHash()));

    }

}
