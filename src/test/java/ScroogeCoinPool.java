public class ScroogeCoinPool {

	/**
	 * @brief Scrooge has right to create coins
	 * 
	 *        In the first transaction, we assume that Scrooge has created 10 coins
	 *        and assigned them to himself, we don’t doubt that because the
	 *        system-Scroogecoin has a building rule which says that Scrooge has
	 *        right to create coins.
	 * 
	 */
	private TransactionInterface genesiseTx;
	private UTXOPool pool;

	public ScroogeCoinPool(ScroogeCoinPeople people) {
		genesiseTx = people.getScrooge().createCoin(10);
		pool = new UTXOPool();
		UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
		pool.addUTXO(utxo, genesiseTx.getOutput(0));
	}

	public TransactionInterface getGenesiseTx() {
		return genesiseTx;
	}

	public UTXOPool getPool() {
		return pool;
	}

}
