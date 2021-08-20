public class InitialCoins {
   
    private Transaction genesiseTx;
    private TxHandlerInterface txHandler;

    public TxHandlerInterface getTxHandler() {
        return txHandler;
    }

    public void GenerateInitialCoins(SampleBitcoinPeople people) {
		genesiseTx = new Transaction();
		genesiseTx.addOutput(10, people.scroogeKeypair.getPublic());
		genesiseTx.finalize();

		UTXOPool pool = new UTXOPool();
		UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
		pool.addUTXO(utxo, genesiseTx.getOutput(0));

		txHandler = new MaxFeeTxHandler(pool);
	}

}
