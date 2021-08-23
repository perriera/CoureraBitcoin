import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

/**
 * @brief interface CoinAuthorityInterface
 * 
 *        Defines a standard way of making trades with ScroogeCoin
 * 
 */

interface CoinAuthorityInterface {

    /**
     * CoinCreatorInterface getCreator();
     * 
     * With ScroogeCoin only one entity is allowed to create coin.
     * 
     * @return
     */
    public CoinCreatorInterface getCreator();

    /**
     * TransactionInterface addCoinForSale
     * 
     * Whenever someone wants to offer their coin to be purchased.
     * 
     * @param tx
     * @param source
     * @param index
     * @return
     */
    public TransactionInterface addCoinForSale(TransactionInterface tx, TransactionInterface source, int index);

    /**
     * TransactionInterface addBuyer
     * 
     * Whoever wants to purchase part of the coin.
     * 
     * @param tx
     * @param amount
     * @param buyer
     * @return
     */
    public TransactionInterface addBuyer(TransactionInterface tx, double amount, CoinOwnerInterface buyer);

    /**
     * TransactionInterface authorizeSale
     * 
     * When the coins and buyers for the transfer is configured, this method
     * authorizes the sale.
     * 
     * @param tx
     * @param seller
     * @param index
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    public TransactionInterface authorizeSale(TransactionInterface tx, CoinOwnerInterface seller, int index)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException;

}

/**
 * Held over from the original code, (provided with this assignment)
 */
interface CoinAuthorityInterfaceLegacy extends CoinAuthorityInterface {

    /**
     * byte[] signMessage
     * 
     * @param sk
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    @Deprecated
    public byte[] signMessage(PrivateKey sk, byte[] message)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException;

}

/**
 * CoinOwnerInterface
 * 
 * Digital Signature
 * 
 * Just like a written signature of a document, but it’s in digital form. The
 * desired features: Only you can sign your own signature Everyone can verify
 * your signature A signature is tied to a certain document, it can’t be copied
 * and used with other documents.
 * 
 * How to Sign and Verify a Digital Signature Generate a Pair of Public Key and
 * Secrete/Private Key (sk, pk) := generates(keysize) The mathematics feature of
 * public/private key pair: Messages encrypted with private can only be
 * decrypted with the public key, and vice versa. You keep the private key to
 * yourself and publish…
 * 
 */
interface CoinOwnerInterface {

    /**
     * PublicKey getPublicKey();
     * 
     * Return the PublicKey for a given coin owner.
     * 
     * @return
     */
    public PublicKey getPublicKey();

    /**
     * PublicKey getPrivateKey();
     * 
     * Return the PrivateKey for a given coin owner.
     * 
     * @return
     */
    public PrivateKey getPrivateKey();

}

/**
 * In the first transaction, we assume that Scrooge has created 10 coins and
 * assigned them to himself, we don’t doubt that because the system-Scroogecoin
 * has a building rule which says that Scrooge has right to create coins.
 */
interface CoinCreatorInterface extends CoinOwnerInterface {

    /**
     * TransactionInterface createCoin
     * 
     * Creates coin to be traded.
     * 
     * @param value
     * @return
     */
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