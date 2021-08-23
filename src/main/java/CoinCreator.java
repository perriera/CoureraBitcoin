import java.security.KeyPairGenerator;

class CoinCreator extends CoinOwner implements CoinCreatorInterface {

    public CoinCreator(KeyPairGenerator keygen) {
        super(keygen);
    }

    @Override
    public TransactionInterface createCoin(double value) {
        Transaction genesiseTx = new Transaction();
        genesiseTx.addOutput(value, getPublicKey());
        genesiseTx.finalize();
        return genesiseTx;
    }

    
}
