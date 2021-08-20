import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TxHandler implements TxHandlerInterface {
	private UTXOPool utxoPool;

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		this.utxoPool = new UTXOPool(utxoPool);
	}

	/**
	 * @return true if: (1) all outputs claimed by {@code tx} are in the current
	 *         UTXO pool, (2) the signatures on each input of {@code tx} are valid,
	 *         (3) no UTXO is claimed multiple times by {@code tx}, (4) all of
	 *         {@code tx}s output values are non-negative, and (5) the sum of
	 *         {@code tx}s input values is greater than or equal to the sum of its
	 *         output values; and false otherwise. //Should the input value and
	 *         output value be equal? Otherwise the ledger will become unbalanced.
	 */
	public boolean isValidTx(Transaction tx) throws Exception {
		Set<UTXO> claimedUTXO = new HashSet<UTXO>();
		double inputSum = 0;
		double outputSum = 0;

		List<InputInterface> inputs = tx.getInputs();
		for (int i = 0; i < inputs.size(); i++) {
			InputInterface input = inputs.get(i);
			ConsumedCoinAvailableException.assertion(utxoPool, input);
			VerifySignatureOfConsumeCoinException.assertion(utxoPool, tx, i, input);
			CoinConsumedMultipleTimesException.assertion(claimedUTXO, input);
			UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
			OutputInterface correspondingOutput = utxoPool.getTxOutput(utxo);
			inputSum += correspondingOutput.getValue();
		}

		List<OutputInterface> outputs = tx.getOutputs();
		for (int i = 0; i < outputs.size(); i++) {
			OutputInterface output = outputs.get(i);
			TransactionOutputLessThanZeroException.assertion(output);
			outputSum += output.getValue();
		}

		TransactionInputSumLessThanOutputSumException.assertion(outputSum, inputSum);

		return true;
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception {
		List<Transaction> acceptedTx = new ArrayList<Transaction>();
		for (int i = 0; i < possibleTxs.length; i++) {
			Transaction tx = possibleTxs[i];
			try {
				if (isValidTx(tx)) {
					acceptedTx.add(tx);

					removeConsumedCoinsFromPool(tx);
					addCreatedCoinsToPool(tx);
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		Transaction[] result = new Transaction[acceptedTx.size()];
		acceptedTx.toArray(result);
		return result;
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
			UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
			utxoPool.removeUTXO(utxo);
		}
	}

}
