import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

class CoinCreator implements CoinCreatorInterface {

    private KeyPair keypair;

    public CoinCreator(KeyPair keypair) {
        this.keypair = keypair;
    }

    @Override
    public TransactionInterface createCoin(double value) {
        Transaction genesiseTx = new Transaction();
        genesiseTx.addOutput(value, getPublicKey());
        genesiseTx.finalize();
        return genesiseTx;
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
