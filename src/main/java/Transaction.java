import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class Transaction implements TransactionInterface {

    public class Input implements InputInterface {
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

        public void addSignature(byte[] sig) {
            if (sig == null)
                signature = null;
            else
                signature = Arrays.copyOf(sig, sig.length);
        }

        public byte[] getPrevTxHash() {
            return prevTxHash;
        }

        public int getOutputIndex() {
            return outputIndex;
        }

        public byte[] getSignature() {
            return signature;
        }

    }

    public class Output implements OutputInterface {
        /** value in bitcoins of the output */
        private double value;

        /** the address or public key of the recipient */
        private PublicKey address;

        public Output(double v, PublicKey addr) {
            value = v;
            address = addr;
        }

        public double getValue() {
            return value;
        }

        public PublicKey getAddress() {
            return address;
        }
    }

    /** hash of the transaction, its unique id */
    private byte[] hash;
    private ArrayList<InputInterface> inputs;
    private ArrayList<OutputInterface> outputs;

    public Transaction() {
        inputs = new ArrayList<InputInterface>();
        outputs = new ArrayList<OutputInterface>();
    }

    public Transaction(Transaction tx) {
        hash = tx.hash.clone();
        inputs = new ArrayList<InputInterface>(tx.inputs);
        outputs = new ArrayList<OutputInterface>(tx.outputs);
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
            InputInterface input = inputs.get(i);
            UTXO u = new UTXO(input);
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
        InputInterface in = inputs.get(index);
        byte[] prevTxHash = in.getPrevTxHash();
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
        b.putInt(in.getOutputIndex());
        byte[] outputIndex = b.array();
        if (prevTxHash != null)
            for (int i = 0; i < prevTxHash.length; i++)
                sigData.add(prevTxHash[i]);
        for (int i = 0; i < outputIndex.length; i++)
            sigData.add(outputIndex[i]);
        for (OutputInterface op : outputs) {
            ByteBuffer bo = ByteBuffer.allocate(Double.SIZE / 8);
            bo.putDouble(op.getValue());
            byte[] value = bo.array();
            byte[] addressBytes = op.getAddress().getEncoded();
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
        for (InputInterface in : inputs) {
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
        for (OutputInterface op : outputs) {
            ByteBuffer b = ByteBuffer.allocate(Double.SIZE / 8);
            b.putDouble(op.getValue());
            byte[] value = b.array();
            byte[] addressBytes = op.getAddress().getEncoded();
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

    public ArrayList<InputInterface> getInputs() {
        return inputs;
    }

    public ArrayList<OutputInterface> getOutputs() {
        return outputs;
    }

    public InputInterface getInput(int index) {
        if (index < inputs.size()) {
            return inputs.get(index);
        }
        return null;
    }

    public OutputInterface getOutput(int index) {
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
