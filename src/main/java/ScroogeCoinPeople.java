import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class ScroogeCoinPeople implements CoinAuthorityInterface {

	private CoinCreatorInterface scroogeKeypair;
	private CoinOwner aliceKeypair;
	private CoinOwner bobKeypair;
	private CoinOwner mikeKeypair;

	public ScroogeCoinPeople() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		scroogeKeypair = new CoinCreator(keyGen.generateKeyPair());
		aliceKeypair = new CoinOwner(keyGen.generateKeyPair());
		bobKeypair = new CoinOwner(keyGen.generateKeyPair());
		mikeKeypair = new CoinOwner(keyGen.generateKeyPair());
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

	public CoinOwner getAlice() {
		return aliceKeypair;
	}

	public CoinOwner getBob() {
		return bobKeypair;
	}

	public CoinOwner getMike() {
		return mikeKeypair;
	}

}