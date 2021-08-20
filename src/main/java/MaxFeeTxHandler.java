import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaxFeeTxHandler extends IsValidHander {

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public MaxFeeTxHandler(UTXOPool utxoPool) {
		super(utxoPool);
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 * 
	 * Sort the accepted transactions by fee
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception {
		List<TransactionWithFee> acceptedTx = new ArrayList<TransactionWithFee>();
		for (Transaction tx : possibleTxs) {
			try {
				if (isValidTx(tx)) {
					TransactionWithFee txWithFee = new TransactionWithFee(tx);
					acceptedTx.add(txWithFee);
					removeConsumedCoinsFromPool(tx);
					addCreatedCoinsToPool(tx);
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		Collections.sort(acceptedTx);
		Transaction[] result = new Transaction[acceptedTx.size()];
		for (int i = 0; i < acceptedTx.size(); i++) {
			result[i] = acceptedTx.get(acceptedTx.size() - i - 1).tx;
		}

		return result;
	}

	class TransactionWithFee implements Comparable<TransactionWithFee> {
		public Transaction tx;
		private double fee;

		public TransactionWithFee(Transaction tx) {
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
	}

	private double calcTxFee(Transaction tx) {
		double inputSum = calculateInputSum(tx);
		double outputSum = calculateOutputSum(tx);

		return inputSum - outputSum;
	}

	private double calculateOutputSum(Transaction tx) {
		double outputSum = 0;
		List<OutputInterface> outputs = tx.getOutputs();
		for (int j = 0; j < outputs.size(); j++) {
			OutputInterface output = outputs.get(j);
			outputSum += output.getValue();
		}
		return outputSum;
	}

	private double calculateInputSum(Transaction tx) {
		List<InputInterface> inputs = tx.getInputs();
		double inputSum = 0;
		for (int j = 0; j < inputs.size(); j++) {
			InputInterface input = inputs.get(j);
			UTXO utxo = new UTXO(input);
			OutputInterface correspondingOutput = utxoPool.getTxOutput(utxo);
			inputSum += correspondingOutput.getValue();
		}
		return inputSum;
	}

	private void addCreatedCoinsToPool(Transaction tx) {
		List<OutputInterface> outputs = tx.getOutputs();
		for (int j = 0; j < outputs.size(); j++) {
			OutputInterface output = outputs.get(j);
			UTXO utxo = new UTXO(tx.getHash(), j);
			utxoPool.addUTXO(utxo, output);
		}
	}

	private void removeConsumedCoinsFromPool(Transaction tx) {
		List<InputInterface> inputs = tx.getInputs();
		for (int j = 0; j < inputs.size(); j++) {
			InputInterface input = inputs.get(j);
			UTXO utxo = new UTXO(input);
			utxoPool.removeUTXO(utxo);
		}
	}

}
