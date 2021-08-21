import java.util.List;

public class TransactionWithFee implements Comparable<TransactionWithFee> {
    public TransactionInterface tx;
    private double fee;
    private UTXOPool utxoPool;

    public TransactionWithFee(UTXOPool utxoPool, TransactionInterface tx) 
    {   
        this.utxoPool = utxoPool;
        this.tx = tx;
        this.fee = calcTxFee(tx);
    }

    @Override
    public int compareTo(TransactionWithFee otherTx) {
        double diff = fee - otherTx.fee;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    private double calcTxFee(TransactionInterface tx) {
        double inputSum = calculateInputSum(tx);
        double outputSum = calculateOutputSum(tx);
        return inputSum - outputSum;
    }

    private double calculateInputSum(TransactionInterface tx) {
        List<InputInterface> inputs = tx.getInputs();
        double inputSum = 0;
        for (InputInterface input : inputs) {
            UTXO utxo = new UTXO(input);
            OutputInterface correspondingOutput = utxoPool.getTxOutput(utxo);
            inputSum += correspondingOutput.getValue();
        }
        return inputSum;
    }

    private double calculateOutputSum(TransactionInterface tx) {
        double outputSum = 0;
        List<OutputInterface> outputs = tx.getOutputs();
        for (OutputInterface output : outputs)
            outputSum += output.getValue();
        return outputSum;
    }

}
