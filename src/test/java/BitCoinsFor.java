public class BitCoinsFor {

	public Transaction getGenesiseTx() {
		return genesiseTx;
	}

	public UTXOPool getPool() {
		return pool;
	}

	private Transaction genesiseTx;
	private UTXOPool pool;

	public BitCoinsFor(SampleBitcoinPeople people) {
		genesiseTx = new Transaction();
		genesiseTx.addOutput(10, people.scroogeKeypair.getPublic());
		genesiseTx.finalize();
		pool = new UTXOPool();
		UTXO utxo = new UTXO(genesiseTx.getHash(), 0);
		pool.addUTXO(utxo, genesiseTx.getOutput(0));

	}

}
