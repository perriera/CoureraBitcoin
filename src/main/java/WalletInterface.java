/**
 * WalletInterface
 * 
 * BSAPI-1322:
 * 
 * GIVEN that a cryptocurrency wallet is a device, physical medium, program or a service which 
 *       stores the public and/or private keys  for cryptocurrency transactions. 
 * WHEN we develop the interfaces[1][4] for this using a good mocking framework[2]
 * THEN we can write the tests before the software, (test driven development[3])
 * 
 */

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

interface WalletPrivateInterface {
    
    public PrivateKey createPrivateKey();
    public void storePrivateKey(PrivateKey privateKey);
    public void retrievePrivateKey(PrivateKey privateKey);
    public void deletePrivateKey(PrivateKey privateKey);

}

interface WalletPublicInterface {
    
    public PublicKey createPublicKey();
    public void storePublicKey(PublicKey publicKey);
    public void retrievePublicKey(PublicKey publicKey);
    public void deletePublicKey(PublicKey privateKey);

}

interface WalletInterface extends WalletPrivateInterface, WalletPublicInterface {

    public List<PublicKey> retrievePublicKeys();
    public List<PrivateKey> retrievePrivateKeys();

}

