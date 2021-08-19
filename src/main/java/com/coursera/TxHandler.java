package com.coursera;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

interface ITxHandler {

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) throws Exception;

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;

}

public class TxHandler implements ITxHandler {

    private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    // public boolean isValidTx(Transaction tx) {
    //     // IMPLEMENT THIS
    //     return true;
    // }

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

		List<IInput> inputs = tx.getInputs();
		for (int i = 0; i < inputs.size(); i++) {
			IInput input = inputs.get(i);

			if (!isConsumedCoinAvailable(input)) {
				return false;
			}

			if (!verifySignatureOfConsumeCoin(tx, i, input)) {
				return false;
			}

			if (isCoinConsumedMultipleTimes(claimedUTXO, input)) {
				return false;
			}

			UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
			IOutput correspondingOutput = utxoPool.getTxOutput(utxo);
			inputSum += correspondingOutput.getValue();

		}

		List<IOutput> outputs = tx.getOutputs();
		for (int i = 0; i < outputs.size(); i++) {
			IOutput output = outputs.get(i);
			if (output.getValue() <= 0) {
				return false;
			}

			outputSum += output.getValue();
		}

		// Should the input value and output value be equal? Otherwise the ledger will
		// become unbalanced.
		// The difference between inputSum and outputSum is the transaction fee
		if (outputSum > inputSum) {
			return false;
		}

		return true;
	}

    /**
     * @brief Zhao's answers
     */

    private boolean isCoinConsumedMultipleTimes(Set<UTXO> claimedUTXO, IInput input) {
		UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
		return !claimedUTXO.add(utxo);
	}
	private boolean verifySignatureOfConsumeCoin(Transaction tx, int index, IInput input) throws Exception {
		UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
		IOutput correspondingOutput = utxoPool.getTxOutput(utxo);
		PublicKey pk = correspondingOutput.geAddress();
		return new Crypto().verifySignature(pk, tx.getRawDataToSign(index), input.getSignature());
	}
	private boolean isConsumedCoinAvailable(IInput input) {
		UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
		return utxoPool.contains(utxo);
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 */
    // public Transaction[] handleTxs(Transaction[] possibleTxs) {
    //     return possibleTxs;
    // }

    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception{
		List<Transaction> acceptedTx = new ArrayList<Transaction>();
		for (int i = 0; i < possibleTxs.length; i++) {
			Transaction tx = possibleTxs[i];
			if (isValidTx(tx)) {
				acceptedTx.add(tx);
				removeConsumedCoinsFromPool(tx);
				addCreatedCoinsToPool(tx);
			}
		}
		Transaction[] result = new Transaction[acceptedTx.size()];
		acceptedTx.toArray(result);
		return result;
	}

	private void addCreatedCoinsToPool(Transaction tx) {
		List<IOutput> outputs = tx.getOutputs();
		for (int j = 0; j < outputs.size(); j++) {
			IOutput output = outputs.get(j);
			UTXO utxo = new UTXO(tx.getHash(), j);
			utxoPool.addUTXO(utxo, output);
		}
	}
	private void removeConsumedCoinsFromPool(Transaction tx) {
		List<IInput> inputs = tx.getInputs();
		for (int j = 0; j < inputs.size(); j++) {
			IInput input = inputs.get(j);
			UTXO utxo = new UTXO(input.getPrevTxHash(), input.getOutputIndex());
			utxoPool.removeUTXO(utxo);
		}
	}

}
