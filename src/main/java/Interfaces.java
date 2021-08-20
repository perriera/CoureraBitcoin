interface Interfaces {

}

interface InputInterface {
    public void addSignature(byte[] sig);
}

interface OutputInterface {

}

interface TxHandlerInterface {
    public boolean isValidTx(Transaction tx) throws Exception;

    public Transaction[] handleTxs(Transaction[] possibleTxs) throws Exception;
}
