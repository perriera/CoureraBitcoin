
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

    public CoinConsumedMultipleTimesException() {
        super("CoinConsumedMultipleTimesException");
    }

    @Override
    public String getIssue() {
        return "CoinConsumedMultipleTimes ";
    }

    static public void assetion(Set<UTXO> claimedUTXO, Transaction.Input input)
            throws CoinConsumedMultipleTimesException {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        if (!claimedUTXO.add(utxo))
            throw new CoinConsumedMultipleTimesException();
    }

}

class VerifySignatureOfConsumeCoinException extends Exceptions {

    public VerifySignatureOfConsumeCoinException() {
        super("VerifySignatureOfConsumeCoinException");
    }

    @Override
    public String getIssue() {
        return "VerifySignatureOfConsumeCoinException ";
    }

    static public void assetion(UTXOPool utxoPool, Transaction tx, int index, Transaction.Input input)
            throws VerifySignatureOfConsumeCoinException {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        PublicKey pk = correspondingOutput.address;
        if (!Crypto.verifySignature(pk, tx.getRawDataToSign(index), input.signature))
            throw new VerifySignatureOfConsumeCoinException();
    }

}

class ConsumedCoinAvailablException extends Exceptions {

    public ConsumedCoinAvailablException() {
        super("ConsumedCoinAvailablException");
    }

    @Override
    public String getIssue() {
        return "ConsumedCoinAvailablException " ;
    }

    static public void assetion(UTXOPool utxoPool, Transaction.Input input) throws ConsumedCoinAvailablException {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        if (!utxoPool.contains(utxo))
            throw new ConsumedCoinAvailablException();
    }

}
