
import java.security.PublicKey;
import java.util.Set;

abstract public class Exceptions extends Exception {

    public Exceptions(String name) {
        super(name);
    }

    public Exceptions() {
        super("Exceptions default constructor");
    }

    abstract public String getIssue();

    static public void diagnostics(Exceptions ex) {
        System.err.println("\n\n");
        System.err.println("\t" + ex.getClass().getSimpleName() + " : " + ex.getMessage());
        System.err.println("\t" + ex.getIssue());
        System.err.println("\n");
    }

}

class CoinConsumedMultipleTimesException extends Exceptions {

    /**
     * 
     */
    private static final long serialVersionUID = -9114856538242344478L;

    String name;

    public CoinConsumedMultipleTimesException(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public String getIssue() {
        return "CoinConsumedMultipleTimes " + name;
    }

    static public void assetion(Set<UTXO> claimedUTXO, Transaction.Input input)
            throws CoinConsumedMultipleTimesException {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        if (!claimedUTXO.add(utxo))
            throw new CoinConsumedMultipleTimesException("this");
    }

}

class VerifySignatureOfConsumeCoinException extends Exceptions {

    /**
     * 
     */
    private static final long serialVersionUID = -9114856538242344478L;

    String name;

    public VerifySignatureOfConsumeCoinException(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public String getIssue() {
        return "CoinConsumedMultipleTimes " + name;
    }

    static public void assetion(UTXOPool utxoPool, Transaction tx, int index, Transaction.Input input)
            throws VerifySignatureOfConsumeCoinException {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        PublicKey pk = correspondingOutput.address;
        if (!Crypto.verifySignature(pk, tx.getRawDataToSign(index), input.signature))
            throw new VerifySignatureOfConsumeCoinException("this");
    }

}
