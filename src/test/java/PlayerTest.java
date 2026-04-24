import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Portfolio;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.TransactionArchive;
import ntnu.gruppe21.transaction.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Player class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the Player class.
 *
 * @author Adrian Balunan
 */
public class PlayerTest {
  private Player player1;

  /* Method to set up a Player instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    player1 = new Player("Player One", new BigDecimal(10000));
  }

  /* Method to verify that the getName() method returns the correct name of the player. */
  @Test
  public void getterNameWorks() {
    assertEquals("Player One", player1.getName());
  }

  /* Method to verify that the getCurrentMoney() method returns the correct current money of the player. */
  @Test
  public void getterCurrentMoneyWorks() {
    assertEquals(new BigDecimal(10000), player1.getCurrentMoney());
  }

  /* Method to verify that the addMoney() method correctly adds money to the player's current money. */
  @Test
  public void amountCannotBeNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          player1.addMoney(null);
        });
  }

  /* Method to verify that the withdrawMoney() method correctly subtracts money from the player's current money. */
  @Test
  public void addMoneyWorks() {
    player1.addMoney(new BigDecimal(10000));
    assertEquals(new BigDecimal(20000), player1.getCurrentMoney());
  }

  /* Method to verify that the withdrawMoney() method correctly subtracts money from the player's current money. */
  @Test
  public void withdrawMoneyWorks() {
    player1.withdrawMoney(new BigDecimal(4000));
    assertEquals(new BigDecimal(6000), player1.getCurrentMoney());
  }

  /* Method to verify that the withdrawMoney() method throws an exception if the player tries to withdraw more money than they have. */
  @Test
  public void getterPortfolioReturnPortfolio() {
    assertInstanceOf(Portfolio.class, player1.getPortfolio());
  }

  /* Method to verify that the getTransactionArchive() method returns the correct TransactionArchive object. */
  @Test
  public void getterArchiveReturnArchive() {
    assertInstanceOf(TransactionArchive.class, player1.getTransactionArchive());
    assertInstanceOf(TransactionArchive.class, player1.getTransactionArchive());
  }

  /* Helper to add a transaction in a given week without affecting player money/portfolio */
  private void addWeeks(Player player, int numberOfWeeks) {
    Stock stock = new Stock("TEST", "TestCo", new BigDecimal(1));
    Share share = new Share(stock, BigDecimal.ONE, BigDecimal.ONE);
    for (int week = 1; week <= numberOfWeeks; week++) {
      player.getTransactionArchive().add(new Purchase(share, week));
    }
  }

  /* New player with no trades should be Novice (1) */
  @Test
  public void getStatusIsNoviceByDefault() {
    assertEquals(1, player1.getStatus());
  }

  /* Player with 20% more money but fewer than 10 weeks should still be Novice */
  @Test
  public void getStatusIsNoviceWithGainButTooFewWeeks() {
    player1.addMoney(new BigDecimal(2000)); // 120% of 10000
    addWeeks(player1, 9);
    assertEquals(1, player1.getStatus());
  }

  /* Player with 10+ weeks but less than 20% gain should still be Novice */
  @Test
  public void getStatusIsNoviceWithEnoughWeeksButInsufficientGain() {
    player1.addMoney(new BigDecimal(1999)); // just under 20%
    addWeeks(player1, 10);
    assertEquals(1, player1.getStatus());
  }

  /* Player with exactly 10 weeks and exactly 20% gain should be Investor (2) */
  @Test
  public void getStatusIsInvestorAtExactThreshold() {
    player1.addMoney(new BigDecimal(2000)); // exactly 20% of 10000
    addWeeks(player1, 10);
    assertEquals(2, player1.getStatus());
  }

  /* Player with more than 10 weeks and more than 20% gain should be Investor */
  @Test
  public void getStatusIsInvestorAboveThreshold() {
    player1.addMoney(new BigDecimal(5000)); // 50% gain
    addWeeks(player1, 15);
    assertEquals(2, player1.getStatus());
  }

  /* Player with 19 weeks and doubled net worth but not quite speculator should not regress */
  @Test
  public void getStatusIsInvestorWith19WeeksAndDoubledNetWorth() {
    player1.addMoney(new BigDecimal(10000)); // doubled
    addWeeks(player1, 19);
    assertEquals(2, player1.getStatus());
  }

  /* Player with exactly 20 weeks and exactly doubled net worth should be Speculator (3) */
  @Test
  public void getStatusIsSpeculatorAtExactThreshold() {
    player1.addMoney(new BigDecimal(10000)); // exactly doubled
    addWeeks(player1, 20);
    assertEquals(3, player1.getStatus());
  }

  /* Player with more than 20 weeks and more than doubled net worth should be Speculator */
  @Test
  public void getStatusIsSpeculatorAboveThreshold() {
    player1.addMoney(new BigDecimal(50000)); // 6x starting
    addWeeks(player1, 25);
    assertEquals(3, player1.getStatus());
  }
}
