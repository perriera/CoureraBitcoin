import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

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

	public byte[] signMessage(PrivateKey sk, byte[] message)
			throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initSign(sk);
		sig.update(message);
		return sig.sign();
	}

}
