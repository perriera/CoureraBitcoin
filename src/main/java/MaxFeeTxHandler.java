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
				isValidTx(tx);
				TransactionWithFee txWithFee = new TransactionWithFee(utxoPool,tx);
				acceptedTx.add(txWithFee);
				removeConsumedCoinsFromPool(tx);
				addCreatedCoinsToPool(tx);
			} catch (Exceptions ex) {
				Exceptions.diagnostics(ex);
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

}
