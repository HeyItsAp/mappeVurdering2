import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.TransactionArchive;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Sale;
import ntnu.gruppe21.transaction.Transaction;
import ntnu.gruppe21.transaction.TransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionArchiveTest {
    private TransactionArchive transactionArchive;
    private TransactionArchive FilledtransactionArchive;
    private Transaction purchase1;

    @BeforeEach
    public void setup(){
        transactionArchive = new TransactionArchive();
        FilledtransactionArchive = new TransactionArchive();
        Stock stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
        Share share = new Share(stock, new BigDecimal(10), new BigDecimal(10));
        purchase1 = new Purchase(share, 1);
        Transaction purchase2 = new Purchase(share, 2);
        Transaction sale1= new Sale(share, 1);
        Transaction sale2 = new Sale(share, 2);
        FilledtransactionArchive.add(purchase2);
        FilledtransactionArchive.add(purchase2);
        FilledtransactionArchive.add(purchase1);
        FilledtransactionArchive.add(purchase1);
        FilledtransactionArchive.add(purchase1);
        FilledtransactionArchive.add(sale2);
        FilledtransactionArchive.add(sale1);
        FilledtransactionArchive.add(sale1);



    }

    @Test
    public void emptyArrayShouldBeCorrect(){
        assertTrue(transactionArchive.isEmpty());
    }

    @Test
    public void addingWorks(){
        transactionArchive.add(purchase1);
        assertFalse(transactionArchive.isEmpty());
    }

    @Test
    public void getterForTransactionsInWeekWorks(){
        assertEquals(5, FilledtransactionArchive.getTransactionsInWeek(1).size());
        assertEquals(3, FilledtransactionArchive.getTransactionsInWeek(2).size());
    }
    @Test
    public void getterForPurchasesInWeekWorks(){
        assertEquals(3, FilledtransactionArchive.getPurchases(1).size());
        assertEquals(2, FilledtransactionArchive.getPurchases(2).size());
    }
    @Test
    public void getterForSalesInWeekWorks(){
        assertEquals(2, FilledtransactionArchive.getSales(1).size());
        assertEquals(1, FilledtransactionArchive.getSales(2).size());
    }
    @Test
    public void countDistinctWeekShouldWork(){
        assertEquals(2, FilledtransactionArchive.countDistinctWeeks());
    }
}
