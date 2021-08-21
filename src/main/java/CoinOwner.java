import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

class CoinOwner implements CoinOwnerInterface {

    private KeyPair keypair;

    public CoinOwner(KeyPair keypair) {
        this.keypair = keypair;
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
