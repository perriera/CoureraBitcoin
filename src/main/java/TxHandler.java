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
		List<Transaction> acceptedTx = new ArrayList<Transaction>();
		for (int i = 0; i < possibleTxs.length; i++) {
			Transaction tx = possibleTxs[i];
			try {
				if (isValidTx(tx)) {
					acceptedTx.add(tx);
					removeConsumedCoinsFromPool(tx);
					addCreatedCoinsToPool(tx);
				}
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
