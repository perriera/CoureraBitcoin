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

    public InputInterface getInput(int index);

    public void removeInput(int index);

    public void removeInput(UTXO ut);

    public ArrayList<InputInterface> getInputs();

    public int numInputs();

}

interface TransactionOutputsInterface {

    public ArrayList<OutputInterface> getOutputs();

    public OutputInterface getOutput(int index);

    public int numOutputs();
}

/**
 * @brief TransactionInterface
 * 
 */
interface TransactionInterface extends CoinDistributerInterface, HashPointerInterface, TransactionInputsInterface,
        TransactionOutputsInterface {

    public byte[] getRawDataToSign(int index);

    @Deprecated
    public void addSignature(byte[] signature, int index);

    public byte[] getRawTx();

    @Deprecated
    public void finalize();

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