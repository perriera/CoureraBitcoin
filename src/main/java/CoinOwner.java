import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

class CoinOwner implements CoinOwnerInterface {

    private KeyPair keypair;

    public CoinOwner(KeyPairGenerator keygen) {
        this.keypair = keygen.generateKeyPair();
    }

    @Override
    public PublicKey getPublicKey() {
        return keypair.getPublic();
    }

    @Override
    public PrivateKey getPrivateKey() {
        return keypair.getPrivate();
    }



}
