import java.security.PublicKey;
import java.util.ArrayList;

interface TransactionInterface {
    public void addInput(byte[] prevTxHash, int outputIndex);

    public void addOutput(double value, PublicKey address);

    public void removeInput(int index);

    public void removeInput(UTXO ut);

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
    public boolean isValidTx(Transaction tx) throws ConsumedCoinAvailableException,
            VerifySignatureOfConsumeCoinException, CoinConsumedMultipleTimesException,
            TransactionOutputLessThanZeroException, TransactionInputSumLessThanOutputSumException;

    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;
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