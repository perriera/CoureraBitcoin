public class BitCoinPool {

	private Transaction genesiseTx;
	private UTXOPool pool;

	public BitCoinPool(BitcoinPeople people) {
		genesiseTx = new Transaction();
		genesiseTx.addOutput(10, people.getScrooge().getPublic());
		genesiseTx.finalize();
		pool = new UTXOPool();
		UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
		pool.addUTXO(utxo, genesiseTx.getOutput(0));

	}

	public Transaction getGenesiseTx() {
		return genesiseTx;
	}

	public UTXOPool getPool() {
		return pool;
	}

}
