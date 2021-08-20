import java.security.PublicKey;

interface Interfaces {

}

interface InputInterface {
    public void addSignature(byte[] sig);

    public byte[] getPrevTxHash();

    public int getOutputIndex();

    public byte[] getSignature();
}

interface OutputInterface {
    public double getValue();

    public PublicKey getAddress();

}

interface TxHandlerInterface {
    public boolean isValidTx(Transaction tx) throws Exception;

    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;
}
