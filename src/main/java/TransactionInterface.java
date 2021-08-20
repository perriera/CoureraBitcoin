import java.security.PublicKey;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

interface TransactionInterface {
    public void addInput(byte[] prevTxHash, int outputIndex) ;

    public void addOutput(double value, PublicKey address) ;

    public void removeInput(int index) ;

    public void removeInput(UTXO ut) ;

    public byte[] getRawDataToSign(int index) ;
    public void addSignature(byte[] signature, int index) ;

    public byte[] getRawTx() ;

    public void finalize() ;

    public void setHash(byte[] h);

    public byte[] getHash() ;

    public ArrayList<InputInterface> getInputs();

    public ArrayList<OutputInterface> getOutputs();

    public InputInterface getInput(int index) ;

    public OutputInterface getOutput(int index);

    public int numInputs() ;

    public int numOutputs() ;

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
    public boolean isValidTx(Transaction tx) throws Exception;

    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;
}
