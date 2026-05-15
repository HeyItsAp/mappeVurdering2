package gameEngine.strategies;

import ntnu.gruppe21.gameEngine.Difficulty;
import ntnu.gruppe21.gameEngine.strategies.StandardStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StandardStrategy.
 *
 * Focus areas:
 *  1. Grace factor dampens volatility correctly during grace period
 *  2. Full volatility applies once grace period has passed
 *  3. Price is always positive (never zero or negative)
 *  4. INSANE (no grace) gets full volatility from week 1
 */
public class StandardStrategyTest {

    private static final BigDecimal BASE_PRICE = new BigDecimal("100.00");

    // ----------------------------------------------------------------
    // Grace factor – EASY (10 weeks)
    // ----------------------------------------------------------------

    @Test
    void gracePeriod_weekOne_shouldProduceDampenedChange_easy() {
        // Week 1 of 10: grace factor = 0.10, so max change = 0.03 * 0.10 = 0.003
        // Over many runs the price must stay within ±0.3% of base
        StandardStrategy strategy =
                new StandardStrategy(Difficulty.EASY, 1);

        double allowedMax = 100.00 * 0.003 + 0.01; // tiny rounding buffer
        for (int i = 0; i < 500; i++) {
            BigDecimal newPrice = strategy.calculateNewPrice(BASE_PRICE);
            double change = Math.abs(newPrice.doubleValue() - 100.00);
            assertTrue(change <= allowedMax,
                    "Week-1 EASY price moved more than allowed: " + newPrice);
        }
    }

    @Test
    void gracePeriod_weekTen_shouldAllowFullVolatility_easy() {
        // Week 10: grace factor = 1.0, max change = ±3%
        StandardStrategy strategy =
                new StandardStrategy(Difficulty.EASY, 10);

        double allowedMax = 100.00 * 0.03 + 0.01;
        for (int i = 0; i < 500; i++) {
            BigDecimal newPrice = strategy.calculateNewPrice(BASE_PRICE);
            double change = Math.abs(newPrice.doubleValue() - 100.00);
            assertTrue(change <= allowedMax,
                    "Week-10 EASY price exceeded ±3%: " + newPrice);
        }
    }

    @Test
    void afterGracePeriod_shouldApplyFullVolatility_easy() {
        // Week 50 (well past grace of 10): same ceiling as week 10
        StandardStrategy week10 =
                new StandardStrategy(Difficulty.EASY, 10);
        StandardStrategy week50 =
                new StandardStrategy(Difficulty.EASY, 50);

        // Both should have graceFactor == 1.0 – verify by comparing
        // max observed changes over many runs (they should be similar)
        double maxChange10 = 0, maxChange50 = 0;
        for (int i = 0; i < 1000; i++) {
            maxChange10 = Math.max(maxChange10,
                    Math.abs(week10.calculateNewPrice(BASE_PRICE).doubleValue() - 100));
            maxChange50 = Math.max(maxChange50,
                    Math.abs(week50.calculateNewPrice(BASE_PRICE).doubleValue() - 100));
        }
        // Both should be close to the same ceiling (within 0.5% of each other)
        assertEquals(maxChange10, maxChange50, 0.5,
                "Post-grace and at-grace should have same effective volatility");
    }

    // ----------------------------------------------------------------
    // INSANE – no grace period
    // ----------------------------------------------------------------

    @Test
    void insane_weekOne_shouldApplyFullVolatilityImmediately() {
        // No grace: factor = 1.0 from the start; max change = ±25%
        StandardStrategy strategy =
                new StandardStrategy(Difficulty.REALISTIC, 1);

        double allowedMax = 100.00 * 0.25 + 0.01;
        for (int i = 0; i < 500; i++) {
            BigDecimal newPrice = strategy.calculateNewPrice(BASE_PRICE);
            double change = Math.abs(newPrice.doubleValue() - 100.00);
            assertTrue(change <= allowedMax,
                    "INSANE week-1 exceeded ±25%: " + newPrice);
        }
    }

    // ----------------------------------------------------------------
    // Price must always stay positive
    // ----------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"EASY", "MEDIUM", "HARD", "REALISTIC"})
    void calculateNewPrice_shouldNeverReturnZeroOrNegative(String difficultyName) {
        Difficulty diff = Difficulty.valueOf(difficultyName);
        StandardStrategy strategy = new StandardStrategy(diff, 1);

        for (int i = 0; i < 1000; i++) {
            BigDecimal newPrice =
                    strategy.calculateNewPrice(BASE_PRICE);
            assertTrue(newPrice.compareTo(BigDecimal.ZERO) > 0,
                    "Price must be positive, was: " + newPrice);
        }
    }

    // ----------------------------------------------------------------
    // Harder difficulty = larger max price change
    // ----------------------------------------------------------------

    @Test
    void harderDifficulty_shouldProduceLargerMaxChange_afterGrace() {
        // Compare EASY vs HARD both well past their grace periods
        StandardStrategy easy  = new StandardStrategy(Difficulty.EASY,   50);
        StandardStrategy hard  = new StandardStrategy(Difficulty.HARD,   50);

        double maxEasy = 0, maxHard = 0;
        for (int i = 0; i < 2000; i++) {
            maxEasy = Math.max(maxEasy,
                    Math.abs(easy.calculateNewPrice(BASE_PRICE).doubleValue() - 100));
            maxHard = Math.max(maxHard,
                    Math.abs(hard.calculateNewPrice(BASE_PRICE).doubleValue() - 100));
        }
        assertTrue(maxHard > maxEasy,
                "HARD should produce larger swings than EASY after grace period");
    }

    // ----------------------------------------------------------------
    // Boundary – week 0 should not crash (defensive)
    // ----------------------------------------------------------------

    @Test
    void calculateNewPrice_weekZero_shouldNotThrow() {
        StandardStrategy strategy =
                new StandardStrategy(Difficulty.MEDIUM, 0);
        assertDoesNotThrow(() ->
                strategy.calculateNewPrice(BASE_PRICE));
    }

    // ----------------------------------------------------------------
    // Null safety
    // ----------------------------------------------------------------

    @Test
    void calculateNewPrice_nullPrice_shouldThrowException() {
        StandardStrategy strategy =
                new StandardStrategy(Difficulty.EASY, 5);
        assertThrows(Exception.class, () ->
                strategy.calculateNewPrice(null));
    }
}