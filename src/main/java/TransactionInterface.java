import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

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
    public void addInput(byte[] prevTxHash, int outputIndex);

    public void removeInput(int index);

    public void removeInput(UTXO ut);

    /**
     * Every transaction has a set of inputs and a set of outputs.
     * 
     * Every output is correlated to the public key of the receiver, which is
     * his/her ScroogeCoin address.
     * 
     * @param value
     * @param address
     */

    public void addOutput(double value, PublicKey address);

}

/**
 * @brief TransactionInterface
 * 
 */
interface TransactionInterface extends CoinDistributerInterface {

    public byte[] getRawDataToSign(int index);

    public void addSignature(byte[] signature, int index);

    public byte[] getRawTx();

    public void finalize();

    public void setHash(byte[] h);

    public byte[] getHash();

    public ArrayList<InputInterface> getInputs();

    public ArrayList<OutputInterface> getOutputs();

    public InputInterface getInput(int index);

    public OutputInterface getOutput(int index);

    public int numInputs();

    public int numOutputs();

}

interface InputInterface {
    public void addSignature(byte[] sig);

    public byte[] getPrevTxHash();

    public int getOutputIndex();

    public byte[] getSignature();
}

interface OutputInterface {
    public double getValue();

    public PublicKey getAddress();

}

interface TxHandlerInterface {
    public boolean isValidTx(TransactionInterface tx) throws ConsumedCoinAvailableException,
            VerifySignatureOfConsumeCoinException, CoinConsumedMultipleTimesException,
            TransactionOutputLessThanZeroException, TransactionInputSumLessThanOutputSumException;

    public TransactionInterface[] handleTxs(TransactionInterface[] possibleTxs) throws Exception;
}

interface UTXOInterface {

    /** @return the transaction hash of this UTXO */
    public byte[] getTxHash();

    /** @return the index of this UTXO */
    public int getIndex();
}

interface UTXOPoolInterface {
    public void addUTXO(UTXO utxo, OutputInterface txOut);

    /** Removes the UTXO {@code utxo} from the pool */
    public void removeUTXO(UTXO utxo);

    /**
     * @return the transaction output corresponding to UTXO {@code utxo}, or null if
     *         {@code utxo} is not in the pool.
     */
    public OutputInterface getTxOutput(UTXO ut);

    /** @return true if UTXO {@code utxo} is in the pool and false otherwise */
    public boolean contains(UTXO utxo);

    /** Returns an {@code ArrayList} of all UTXOs in the pool */
    public ArrayList<UTXO> getAllUTXO();

}

interface CryptoInterface {
    public boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature);
}