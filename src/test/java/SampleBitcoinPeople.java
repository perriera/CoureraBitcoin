import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class SampleBitcoinPeople {
    
    public KeyPair scroogeKeypair;
	public KeyPair aliceKeypair;
	public KeyPair bobKeypair;
	public KeyPair mikeKeypair;
    
    public SampleBitcoinPeople() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		scroogeKeypair = keyGen.generateKeyPair();
		aliceKeypair = keyGen.generateKeyPair();
		bobKeypair = keyGen.generateKeyPair();
		mikeKeypair = keyGen.generateKeyPair();
    }

}
