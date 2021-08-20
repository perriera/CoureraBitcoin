import org.junit.Before;

public class TxHandlerTest extends BasicHandlerTests {

	@Before
	public void setUp() throws Exception {
		people = new BitcoinPeople();
		bitcoins = new BitCoinPool(people);
		txHandler = new TxHandler(bitcoins.getPool());
	}


}
