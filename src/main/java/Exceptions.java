
import java.security.PublicKey;
import java.util.Set;

abstract public class Exceptions extends Exception {

    public Exceptions(String name) {
        super(name);
    }

    static public void diagnostics(Exceptions ex) {
        System.err.println("\n\n");
        System.err.println("\t" + ex.getClass().getSimpleName() + " : " + ex.getMessage());
        System.err.println("\n");
    }

}

class CoinConsumedMultipleTimesException extends Exceptions {

    public CoinConsumedMultipleTimesException() {
        super("CoinConsumedMultipleTimesException");
    }

    static public void assertion(Set<UTXO> claimedUTXO, Transaction.Input input)
            throws CoinConsumedMultipleTimesException {
        UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
        if (!claimedUTXO.add(utxo))
            throw new CoinConsumedMultipleTimesException();
    }

}

class VerifySignatureOfConsumeCoinException extends Exceptions {

    public VerifySignatureOfConsumeCoinException() {
        super("VerifySignatureOfConsumeCoinException");
    }

    static public void assertion(UTXOPool utxoPool, Transaction tx, int index, Transaction.Input input)
            throws VerifySignatureOfConsumeCoinException {
        UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        PublicKey pk = correspondingOutput.getAddress();
        if (!Crypto.verifySignature(pk, tx.getRawDataToSign(index), input.getSignature()))
            throw new VerifySignatureOfConsumeCoinException();
    }

}

class ConsumedCoinAvailableException extends Exceptions {

    public ConsumedCoinAvailableException() {
        super("ConsumedCoinAvailableException");
    }

    static public void assertion(UTXOPool utxoPool, Transaction.Input input) throws ConsumedCoinAvailableException {
        UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
        if (!utxoPool.contains(utxo))
            throw new ConsumedCoinAvailableException();
    }

}

class TransactionOutputLessThanZeroException extends Exceptions {

    public TransactionOutputLessThanZeroException() {
        super("TransactionOutputLessThanZeroException");
    }

    static public void assertion(Transaction.Output output) throws TransactionOutputLessThanZeroException {
        if (output.getValue()<0)
            throw new TransactionOutputLessThanZeroException();
    }

}

class TransactionInputSumLessThanOutputSumException extends Exceptions {

    public TransactionInputSumLessThanOutputSumException() {
        super("TransactionOutputSumLessThanInputSumException");
    }

    static public void assertion(double outputSum, double inputSum) throws TransactionInputSumLessThanOutputSumException {
        if (outputSum>inputSum)
            throw new TransactionInputSumLessThanOutputSumException();
    }

}
