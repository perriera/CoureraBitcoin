import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class ScroogeCoinAuthority implements CoinAuthorityInterfaceLegacy {

	private CoinCreatorInterface scroogeKeypair;
	private CoinOwnerInterface aliceKeypair;
	private CoinOwnerInterface bobKeypair;
	private CoinOwnerInterface mikeKeypair;

	public ScroogeCoinAuthority() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		scroogeKeypair = new CoinCreator(keyGen);
		aliceKeypair = new CoinOwner(keyGen);
		bobKeypair = new CoinOwner(keyGen);
		mikeKeypair = new CoinOwner(keyGen);
	}

	public byte[] signMessage(PrivateKey sk, byte[] message)
			throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initSign(sk);
		sig.update(message);
		return sig.sign();
	}

	public CoinCreatorInterface getCreator() {
		return scroogeKeypair;
	}

	public CoinOwnerInterface getAlice() {
		return aliceKeypair;
	}

	public CoinOwnerInterface getBob() {
		return bobKeypair;
	}

	public CoinOwnerInterface getMike() {
		return mikeKeypair;
	}

	@Override
    public TransactionInterface addCoinForSale(TransactionInterface tx, TransactionInterface source, int index) {
        tx.addInput(source.getHash(), index);
        return tx;
    }

    @Override
    public TransactionInterface addBuyer(TransactionInterface tx, double amount, CoinOwnerInterface buyer) {
        tx.addOutput(amount, buyer.getPublicKey());
        return tx;
    }

    @Override
    public TransactionInterface authorizeSale(TransactionInterface tx, CoinOwnerInterface seller, int index)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
        byte[] authorization = signMessage(seller.getPrivateKey(), tx.getRawDataToSign(index));
        tx.addSignature(authorization, 0);
        tx.finalize();
        return tx;
    }
	
}
