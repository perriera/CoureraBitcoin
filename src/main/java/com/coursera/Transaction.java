package com.coursera;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

interface IInput {
    /**
     * @brief getPrevTxHash();
     * @return
     */
    public byte[] getPrevTxHash();

    /**
     * @brief getOutputIndex()
     * 
     * used output's index in the previous transaction 
     * @return
     */
    public int getOutputIndex();

    /**
     * @brief getSignature();
     * 
     * the signature produced to check validity
     * 
     * @return
     */
    public byte[] getSignature();

    /**
     * @brief addSignature(byte[] sig)
     * @param sig
     */
    public void addSignature(byte[] sig);
};

interface IOutput {
    public double getValue();
    public PublicKey geAddress();
};

interface ITransaction {
    public void addInput(byte[] prevTxHash, int outputIndex);

    public void addOutput(double value, PublicKey address);

    public void removeInput(int index);

    public void removeInput(UTXO ut);

    public byte[] getRawDataToSign(int index);

    public void addSignature(byte[] signature, int index);

    public byte[] getRawTx();

    public void setHash(byte[] h);

    public byte[] getHash();

    public ArrayList<IInput> getInputs();

    public ArrayList<IOutput> getOutputs();

    public IInput getInput(int index);

    public IOutput getOutput(int index);

    public int numInputs();

    public int numOutputs();
};

public class Transaction implements ITransaction {

    public class Input implements IInput {
        /** hash of the Transaction whose output is being used */
        private byte[] prevTxHash;
        /** used output's index in the previous transaction */
        private int outputIndex;
        /** the signature produced to check validity */
        private byte[] signature;

        public Input(byte[] prevHash, int index) {
            if (prevHash == null)
                prevTxHash = null;
            else
                prevTxHash = Arrays.copyOf(prevHash, prevHash.length);
            outputIndex = index;
        }

        public byte[] getPrevTxHash() { return prevTxHash; }

        /** used output's index in the previous transaction */
        public int getOutputIndex() { return outputIndex; }
    
        /** the signature produced to check validity */
        public byte[] getSignature() { return signature; }
    
        public void addSignature(byte[] sig) {
            if (sig == null)
                signature = null;
            else
                signature = Arrays.copyOf(sig, sig.length);
        }
    }

    public class Output implements IOutput {
        /** value in bitcoins of the output */
        private double value;
        /** the address or public key of the recipient */
        private PublicKey address;

        public double getValue() { return value;}
        public PublicKey geAddress() { return address;}
        public Output(double v, PublicKey addr) {
            value = v;
            address = addr;
        }
    }

    /** hash of the transaction, its unique id */
    private byte[] hash;
    private ArrayList<IInput> inputs;
    private ArrayList<IOutput> outputs;

    public Transaction() {
        inputs = new ArrayList<IInput>();
        outputs = new ArrayList<IOutput>();
    }

    public Transaction(Transaction tx) {
        hash = tx.hash.clone();
        inputs = new ArrayList<IInput>(tx.inputs);
        outputs = new ArrayList<IOutput>(tx.outputs);
    }

    public void addInput(byte[] prevTxHash, int outputIndex) {
        Input in = new Input(prevTxHash, outputIndex);
        inputs.add(in);
    }

    public void addOutput(double value, PublicKey address) {
        Output op = new Output(value, address);
        outputs.add(op);
    }

    public void removeInput(int index) {
        inputs.remove(index);
    }

    public void removeInput(UTXO ut) {
        for (int i = 0; i < inputs.size(); i++) {
            IInput in = inputs.get(i);
            UTXO u = new UTXO(in.getPrevTxHash(), in.getOutputIndex());
            if (u.equals(ut)) {
                inputs.remove(i);
                return;
            }
        }
    }

    public byte[] getRawDataToSign(int index) {
        // ith input and all outputs
        ArrayList<Byte> sigData = new ArrayList<Byte>();
        if (index > inputs.size())
            return null;
        IInput in = inputs.get(index);
        byte[] prevTxHash = in.getPrevTxHash();
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
        b.putInt(in.getOutputIndex());
        byte[] outputIndex = b.array();
        if (prevTxHash != null)
            for (int i = 0; i < prevTxHash.length; i++)
                sigData.add(prevTxHash[i]);
        for (int i = 0; i < outputIndex.length; i++)
            sigData.add(outputIndex[i]);
        for (IOutput op : outputs) {
            ByteBuffer bo = ByteBuffer.allocate(Double.SIZE / 8);
            bo.putDouble(op.getValue());
            byte[] value = bo.array();
            byte[] addressBytes = op.geAddress().getEncoded();
            for (int i = 0; i < value.length; i++)
                sigData.add(value[i]);

            for (int i = 0; i < addressBytes.length; i++)
                sigData.add(addressBytes[i]);
        }
        byte[] sigD = new byte[sigData.size()];
        int i = 0;
        for (Byte sb : sigData)
            sigD[i++] = sb;
        return sigD;
    }

    public void addSignature(byte[] signature, int index) {
        inputs.get(index).addSignature(signature);
    }

    public byte[] getRawTx() {
        ArrayList<Byte> rawTx = new ArrayList<Byte>();
        for (IInput in : inputs) {
            byte[] prevTxHash = in.getPrevTxHash();
            ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
            b.putInt(in.getOutputIndex());
            byte[] outputIndex = b.array();
            byte[] signature = in.getSignature();
            if (prevTxHash != null)
                for (int i = 0; i < prevTxHash.length; i++)
                    rawTx.add(prevTxHash[i]);
            for (int i = 0; i < outputIndex.length; i++)
                rawTx.add(outputIndex[i]);
            if (signature != null)
                for (int i = 0; i < signature.length; i++)
                    rawTx.add(signature[i]);
        }
        for (IOutput op : outputs) {
            ByteBuffer b = ByteBuffer.allocate(Double.SIZE / 8);
            b.putDouble(op.getValue());
            byte[] value = b.array();
            byte[] addressBytes = op.geAddress().getEncoded();
            for (int i = 0; i < value.length; i++) {
                rawTx.add(value[i]);
            }
            for (int i = 0; i < addressBytes.length; i++) {
                rawTx.add(addressBytes[i]);
            }

        }
        byte[] tx = new byte[rawTx.size()];
        int i = 0;
        for (Byte b : rawTx)
            tx[i++] = b;
        return tx;
    }

    public void finalize() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(getRawTx());
            hash = md.digest();
        } catch (NoSuchAlgorithmException x) {
            x.printStackTrace(System.err);
        }
    }

    public void setHash(byte[] h) {
        hash = h;
    }

    public byte[] getHash() {
        return hash;
    }

    public ArrayList<IInput> getInputs() {
        return inputs;
    }

    public ArrayList<IOutput> getOutputs() {
        return outputs;
    }

    public IInput getInput(int index) {
        if (index < inputs.size()) {
            return inputs.get(index);
        }
        return null;
    }

    public IOutput getOutput(int index) {
        if (index < outputs.size()) {
            return outputs.get(index);
        }
        return null;
    }

    public int numInputs() {
        return inputs.size();
    }

    public int numOutputs() {
        return outputs.size();
    }
}
