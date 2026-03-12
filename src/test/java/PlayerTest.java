import ntnu.gruppe21.Player;
import ntnu.gruppe21.Portfolio;
import ntnu.gruppe21.TransactionArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Player class.
 * 
 * The tests currently focus on basic functionality and expected behavior of the Player class.
 * @author Adrian Balunan
 */

public class PlayerTest {
    private Player player1;

    /* Method to set up a Player instance before each test case is executed, ensuring a consistent test environment. */
    @BeforeEach
    public void setup(){
        player1 = new Player("Player One", new BigDecimal(10000));
    }

    /* Method to verify that the getName() method returns the correct name of the player. */
    @Test
    public void getterNameWorks(){
        assertEquals("Player One", player1.getName());
    }

    /* Method to verify that the getCurrentMoney() method returns the correct current money of the player. */
    @Test
    public void getterCurrentMoneyWorks(){
        assertEquals(new BigDecimal(10000), player1.getCurrentMoney());
    }

    /* Method to verify that the addMoney() method correctly adds money to the player's current money. */
    @Test
    public void amountCannotBeNull(){
        assertThrows(NullPointerException.class, () -> {
            player1.addMoney(null);
        });
    }

    /* Method to verify that the withdrawMoney() method correctly subtracts money from the player's current money. */
    @Test
    public void addMoneyWorks(){
        player1.addMoney(new BigDecimal(10000));
        assertEquals(new BigDecimal(20000), player1.getCurrentMoney());
    }

    /* Method to verify that the withdrawMoney() method correctly subtracts money from the player's current money. */
    @Test
    public void withdrawMoneyWorks(){
        player1.withdrawMoney(new BigDecimal(4000));
        assertEquals(new BigDecimal(6000), player1.getCurrentMoney());
    }

    /* Method to verify that the withdrawMoney() method throws an exception if the player tries to withdraw more money than they have. */
    @Test
    public void getterPortfolioReturnPortfolio(){
        assertInstanceOf(Portfolio.class, player1.getPortfolio());
    }

    /* Method to verify that the getTransactionArchive() method returns the correct TransactionArchive object. */
    @Test
    public void getterArchiveReturnArchive(){
        assertInstanceOf(TransactionArchive.class, player1.getTransactionArchive());
        assertInstanceOf(TransactionArchive.class, player1.getTransactionArchive());

    }


}
