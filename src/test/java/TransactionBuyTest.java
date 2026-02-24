import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Transaction;
import ntnu.gruppe21.transaction.TransactionException;
import ntnu.gruppe21.transaction.calculators.PurchaseCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionBuyTest {
    private Transaction purchase;
    private Share share;
    private Player player1;

    @BeforeEach
    public void setup(){
        Stock stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
        share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
        player1 = new Player("Name", new BigDecimal(100000));
        purchase = new Purchase(share, 1);
    }

    @Test
    public void getterForShareWorks(){
        assertEquals(share, purchase.getShare());
    }

    @Test
    public void getterForWeekWorks(){
        assertEquals(1, purchase.getWeek());
    }

    @Test
    public void getterForCalculatorReturnCorrectType(){
        assertInstanceOf(PurchaseCalculator.class, purchase.getCalculator());
    }

    @Test
    public void IsCommitedWorks(){
        assertFalse(purchase.isCommitted());
    }

    @Test
    public void commitingWorks() throws TransactionException {
        purchase.commit(player1);
        assertTrue(purchase.isCommitted());
        assertThrows(TransactionException.class, () -> {
           purchase.commit(player1);
        });
    }

}
