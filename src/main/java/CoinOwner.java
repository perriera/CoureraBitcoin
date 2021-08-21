import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

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

    @Override
    public TransactionInterface addCoin(TransactionInterface tx, TransactionInterface source, int index) {
        tx.addInput(source.getHash(), index);
        return tx;
    }

    @Override
    public TransactionInterface addBuyer(TransactionInterface tx, double amount, CoinOwnerInterface buyer) {
        tx.addOutput(amount, buyer.getPublicKey());
        return tx;
    }

    @Override
    public TransactionInterface authorizeSale(TransactionInterface tx, CoinOwnerInterface seller, int index,
            CoinAuthorityInterface authority)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
        byte[] authorization = authority.signMessage(seller.getPrivateKey(), tx.getRawDataToSign(index));
        tx.addSignature(authorization, 0);
        tx.finalize();
        return tx;
    }

}
