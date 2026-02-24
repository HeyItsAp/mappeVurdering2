import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Sale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExhangeTest {
    private Exchange exchange;
    private Stock stock1;

    @BeforeEach
    public void setup(){
        stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
        Stock stock2 = new Stock("DogeCoin", "Company2", new BigDecimal(10));
        Stock stock3 = new Stock("DopeCoin", "Company3", new BigDecimal(500));
        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock1);
        stocks.add(stock2);
        stocks.add(stock3);
        exchange = new Exchange("Oslo Børs", stocks);
    }

    @Test
    public void getterNameWorks(){
        assertEquals("Oslo Børs", exchange.getName());
    }

    @Test
    public void getterWeekWorks(){
        assertEquals(1, exchange.getWeek());
    }

    @Test
    public void getStockWithSymbolStockReturns(){
        assertEquals(stock1, exchange.getStock("Bit"));
    }

    @Test
    public void hasStockWorks(){
        assertTrue(exchange.hasStock("Bit"));
    }

    @Test
    public void getStockWithSymbolThrowsExecptionIfNotFound(){
        assertThrows(IllegalArgumentException.class, () -> {
           exchange.getStock("SymbolThatDoesNotExist");
        });
    }

    @Test
    public void findStockWorks(){
        List<Stock> expectedResult = new ArrayList<>();
        expectedResult.add(stock1);
        assertEquals(expectedResult, exchange.findStock("Bi"));
    }

    @Test
    public void findStockReturnsEmptyInFailure(){
        List<Stock> expectedResult = new ArrayList<>();
        assertEquals(expectedResult, exchange.findStock("StockDoesNotExist"));
    }

    @Test
    public void buyingReturnsCorrectType(){
        Player player = new Player("Name1", new BigDecimal(10000000));
        assertInstanceOf(Purchase.class, exchange.buy("Bit", new BigDecimal(10), player));
    }

    @Test
    public void buyingThrowsExceptionIfNotEnoughMoney(){
        Share share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
        Purchase purchase = new Purchase(share, 1);
        Player player = new Player("Name1", new BigDecimal(1));

        assertThrows(IllegalArgumentException.class,() -> {
            exchange.buy("Bit", new BigDecimal(10), player);
        });
    }

    @Test
    public void saleReturnsCorrectType(){
        Player player = new Player("Name1", new BigDecimal(10000000));
        assertInstanceOf(Sale.class, exchange.sell("Bit", new BigDecimal(10), player));
    }

    @Test
    public void advanceWorks(){
        java.math.BigDecimal oldprice = exchange.getStock("Bit").getSalesPrice();
        exchange.advance();
        assertNotEquals(oldprice, exchange.getStock("Bit").getSalesPrice());
        assertEquals(2, exchange.getWeek());
    }
}
