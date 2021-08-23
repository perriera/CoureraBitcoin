import java.security.PublicKey;
import java.util.ArrayList;

/**
 * interface HashPointerInterface
 * 
 * Hash Pointer is comprised of two parts: Pointer to where some information is
 * stored Cryptographic hash of that information The pointer can be used to get
 * the information, the hash can be used to verify that information hasn’t been
 * changed
 * 
 */
interface HashPointerInterface {

    /**
     * set the hash value
     * 
     * @param h
     */
    public void setHash(byte[] h);

    /**
     * get the hash value
     * 
     * @return
     */
    public byte[] getHash();

}

interface TransactionInputsInterface {

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

    public InputInterface getInput(int index);

    public void removeInput(int index);

    public void removeInput(UTXO ut);

    public ArrayList<InputInterface> getInputs();

    public int numInputs();

}

interface TransactionOutputsInterface {

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

    public ArrayList<OutputInterface> getOutputs();

    public OutputInterface getOutput(int index);

    public int numOutputs();
}

/**
 * @brief TransactionInterface
 * 
 */
interface TransactionInterface extends HashPointerInterface, TransactionInputsInterface, TransactionOutputsInterface {

    /**
     * 
     * The following methods are used to serialize the data inside the Transaction
     * object. At the basic level the values and signatures the compose the
     * transaction are all reduce to a simple byte[] array.
     * 
     */
    public byte[] getRawDataToSign(int index);

    @Deprecated
    public void addSignature(byte[] signature, int index);

    public byte[] getRawTx();

    @Deprecated
    public void finalize();

}

/**
 * InputInterface
 * 
 * Anyone offing coin for sale provides the following methods.
 * 
 */
interface InputInterface {
    public void addSignature(byte[] sig);

    public byte[] getPrevTxHash();

    public int getOutputIndex();

    public byte[] getSignature();
}

/**
 * OutputInterface
 * 
 * Anyone looking for purchase coin provides the following methods.
 * 
 */
interface OutputInterface {
    public double getValue();

    public PublicKey getAddress();

}

/**
 * TxHandlerInterface
 * 
 * This interface defines the method necessary to process a series of
 * Transaction objects. A variety of exceptions maybe thrown depending on the
 * content of the inputs and outputs.
 * 
 */
interface TxHandlerInterface {

    /**
     * boolean isValidTx
     * 
     * Tests each individual trasaction for validity.
     * 
     * @param tx
     * @return
     * @throws ConsumedCoinAvailableException
     * @throws VerifySignatureOfConsumeCoinException
     * @throws CoinConsumedMultipleTimesException
     * @throws TransactionOutputLessThanZeroException
     * @throws TransactionInputSumLessThanOutputSumException
     */
    public boolean isValidTx(TransactionInterface tx) throws ConsumedCoinAvailableException,
            VerifySignatureOfConsumeCoinException, CoinConsumedMultipleTimesException,
            TransactionOutputLessThanZeroException, TransactionInputSumLessThanOutputSumException;

    /**
     * handleTxs()
     * 
     * Processes an array of transactions, (using the above method).
     * 
     * @param possibleTxs
     * @return
     * @throws Exception
     */
    public TransactionInterface[] handleTxs(TransactionInterface[] possibleTxs) throws Exception;
}

/**
 * UTXOInterface
 * 
 * Used to determine the hash of a given transaction.
 * 
 */
interface UTXOInterface {

    /** @return the transaction hash of this UTXO */
    public byte[] getTxHash();

    /** @return the index of this UTXO */
    public int getIndex();
}

/**
 * UTXOPoolInterface
 * 
 * Used to manage a group of UTXOInterface instances.
 * 
 */
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

/**
 * CryptoInterface
 * 
 * Used to verify the signature of a transaction.
 * 
 */
interface CryptoInterface {
    public boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature);
}