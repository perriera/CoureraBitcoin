import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

interface CoinAuthorityInterface {
    @Deprecated
    public byte[] signMessage(PrivateKey sk, byte[] message)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException;

    public CoinCreatorInterface getCreator();

    public TransactionInterface addCoinForSale(TransactionInterface tx, TransactionInterface source, int index);

    public TransactionInterface addBuyer(TransactionInterface tx, double amount, CoinOwnerInterface buyer);

    public TransactionInterface authorizeSale(TransactionInterface tx, CoinOwnerInterface seller, int index)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException;

}

interface CoinOwnerInterface {
    public PublicKey getPublicKey();

    public PrivateKey getPrivateKey();

}

interface CoinCreatorInterface extends CoinOwnerInterface {
    public TransactionInterface createCoin(double value);

}

interface CoinDistributerInterface {

    /**
     * Every transaction has a set of inputs and a set of outputs.
     * 
     * An input in a transaction must use a hash pointer to refer to its
     * corresponding output in the previous transaction, and it must be signed with
     * the private key of the owner because the owner needs to prove he/she agrees
     * to spend his/her coins.
     * 
     * @param prevTxHash
     * @param outputIndex
     */
    @Deprecated
    public void addInput(byte[] prevTxHash, int outputIndex);

    /**
     * Every transaction has a set of inputs and a set of outputs.
     * 
     * Every output is correlated to the public key of the receiver, which is
     * his/her ScroogeCoin address.
     * 
     * @param value
     * @param address
     */

    @Deprecated
    public void addOutput(double value, PublicKey address);

}