import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Portfolio;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeType;
import ntnu.gruppe21.model.transaction.Purchase;
import ntnu.gruppe21.model.transaction.TransactionArchive;
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
    player1 = new Player.Builder("Player One", new BigDecimal(10000), Difficulty.EASY).build();
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
    Share share = new Share(stock, 1, BigDecimal.ONE);
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
    player1.addMoney(
        new BigDecimal(
            1980)); // just under 20% because of easy difficulty (adding a 1.01 multiplier)
    addWeeks(player1, 10);
    assertEquals(1, player1.getStatus());
  }

  private void simulateCompletions(int n) {
    player1
        .getChallengeManager()
        .parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, n), player1);
    player1.getChallengeManager().setTotalCompletions(n);
  }

  /* Player with exactly 3 challenge completions should be Investor (2) */
  @Test
  public void getStatusIsInvestorAtExactThreshold() {
    simulateCompletions(3);
    assertEquals(2, player1.getStatus());
  }

  /* Player with more than 3 but fewer than 8 completions should be Investor */
  @Test
  public void getStatusIsInvestorAboveThreshold() {
    simulateCompletions(5);
    assertEquals(2, player1.getStatus());
  }

  /* Player with 7 completions (just below Speculator threshold) should remain Investor */
  @Test
  public void getStatusIsInvestorBeforeSpeculatorThreshold() {
    simulateCompletions(7);
    assertEquals(2, player1.getStatus());
  }

  /* Player with exactly 8 challenge completions should be Speculator (3) */
  @Test
  public void getStatusIsSpeculatorAtExactThreshold() {
    simulateCompletions(8);
    assertEquals(3, player1.getStatus());
  }

  /* Player with more than 8 completions should remain Speculator */
  @Test
  public void getStatusIsSpeculatorAboveThreshold() {
    simulateCompletions(12);
    assertEquals(3, player1.getStatus());
  }
}
