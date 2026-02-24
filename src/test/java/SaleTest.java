import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.transaction.Sale;
import ntnu.gruppe21.transaction.Transaction;
import ntnu.gruppe21.transaction.TransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaleTest {
    private Transaction sale;
    private Player player1;

    @BeforeEach
    public void setup(){
        Stock stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
        Share share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
        player1 = new Player("Name", new BigDecimal(100000));
        sale = new Sale(share, 1);
    }

    @Test
    public void commitingWorks() throws TransactionException {
        sale.commit(player1);
        assertTrue(sale.isCommitted());
        assertThrows(TransactionException.class, () -> {
            sale.commit(player1);
        });
    }

}
