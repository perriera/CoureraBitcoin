

import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract public class IsValidHander implements TxHandlerInterface {
	protected UTXOPool utxoPool;

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public IsValidHander(UTXOPool utxoPool) {
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
    public boolean isValidTx(Transaction tx) throws ConsumedCoinAvailableException,
            VerifySignatureOfConsumeCoinException, CoinConsumedMultipleTimesException,
            TransactionOutputLessThanZeroException, TransactionInputSumLessThanOutputSumException {
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
	abstract public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;


}
