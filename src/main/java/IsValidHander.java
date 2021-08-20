
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
	 * @brief isValidTx()
	 * 
	 *        Should the input value and output value be equal? Otherwise the ledger
	 *        will become unbalanced.
	 * 
	 *        true if:
	 * 
	 * @implNote (1) all outputs claimed by {@code tx} are in the current UTXO pool,
	 * @implNote (2) the signatures on each input of {@code tx} are valid,
	 * @implNote (3) no UTXO is claimed multiple times by {@code tx},
	 * @implNote (4) all of {@code tx}s output values are non-negative, and
	 * @implNote (5) the sum of {@code tx}s input values is greater than or equal to
	 *           the sum of its output values; and false otherwise.
	 * 
	 * @throws ConsumedCoinAvailableException,
	 * @throws VerifySignatureOfConsumeCoinException,
	 * @throws CoinConsumedMultipleTimesException,
	 * @throws TransactionOutputLessThanZeroException,
	 * @throws TransactionInputSumLessThanOutputSumException
	 * 
	 */
	public boolean isValidTx(Transaction tx) throws ConsumedCoinAvailableException,
			VerifySignatureOfConsumeCoinException, CoinConsumedMultipleTimesException,
			TransactionOutputLessThanZeroException, TransactionInputSumLessThanOutputSumException {
		Set<UTXO> claimedUTXO = new HashSet<UTXO>();
		double inputSum = 0;
		double outputSum = 0;

		int i=0;
		for (InputInterface input : tx.getInputs()) {
			ConsumedCoinAvailableException.assertion(utxoPool, input);
			VerifySignatureOfConsumeCoinException.assertion(utxoPool, tx, i++, input);
			CoinConsumedMultipleTimesException.assertion(claimedUTXO, input);
			UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
			OutputInterface correspondingOutput = utxoPool.getTxOutput(utxo);
			inputSum += correspondingOutput.getValue();
		}

		for (OutputInterface output :tx.getOutputs()) {
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
