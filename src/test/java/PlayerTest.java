import ntnu.gruppe21.Player;
import ntnu.gruppe21.Portfolio;
import ntnu.gruppe21.TransactionArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player1;

    @BeforeEach
    public void setup(){
        player1 = new Player("Player One", new BigDecimal(10000));
    }

    @Test
    public void getterNameWorks(){
        assertEquals("Player One", player1.getName());
    }

    @Test
    public void getterCurrentMoneyWorks(){
        assertEquals(new BigDecimal(10000), player1.getCurrentMoney());
    }

    @Test
    public void amountCannotBeNull(){
        assertThrows(NullPointerException.class, () -> {
            player1.addMoney(null);
        });
    }

    @Test
    public void addMoneyWorks(){
        player1.addMoney(new BigDecimal(10000));
        assertEquals(new BigDecimal(20000), player1.getCurrentMoney());
    }

    @Test
    public void withdrawMoneyWorks(){
        player1.withdrawMoney(new BigDecimal(4000));
        assertEquals(new BigDecimal(6000), player1.getCurrentMoney());
    }

    @Test
    public void getterPortfolioReturnPortfolio(){
        assertInstanceOf(Portfolio.class, player1.getPortfolio());
    }

    @Test
    public void getterArchiveReturnArchive(){
        assertInstanceOf(TransactionArchive.class, player1.getTransactionArchive());
    }


}
