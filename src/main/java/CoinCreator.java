import java.security.KeyPair;

class CoinCreator extends CoinOwner implements CoinCreatorInterface {

    public CoinCreator(KeyPair keypair) {
        super(keypair);
    }

    @Override
    public TransactionInterface createCoin(double value) {
        Transaction genesiseTx = new Transaction();
        genesiseTx.addOutput(value, getPublicKey());
        genesiseTx.finalize();
        return genesiseTx;
    }

    
}
