import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxHandler extends IsValidHander {

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		super(utxoPool);
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 * 
	 * Don't sort the accepted transactions by fee
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception {
		List<TransactionInterface> acceptedTx = new ArrayList<TransactionInterface>();
		for (Transaction tx : possibleTxs) {
			try {
				isValidTx(tx);
				acceptedTx.add(tx);
				removeConsumedCoinsFromPool(tx);
				addCreatedCoinsToPool(tx);
			} catch (Exceptions ex) {
				Exceptions.diagnostics(ex);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		Transaction[] result = new Transaction[acceptedTx.size()];
		acceptedTx.toArray(result);
		return result;
	}

}
