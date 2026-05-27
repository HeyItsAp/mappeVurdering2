package ntnu.gruppe21.model.gameEngine.strategies.marketsimulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.strategies.PriceStrategy;

/**
 * Java translation of the Cookie Clicker bank minigame's stock price advancement logic
 * (M.calculateNewPrice).
 */
public class MarketSimulator implements PriceStrategy {

  // Market modes — each biases momentum in a different direction.
  public static final int MODE_STABLE = 0; // slow random walk around resting value
  public static final int MODE_SLOW_RISE = 1; // gradual upward bias
  public static final int MODE_SLOW_FALL = 2; // gradual downward bias
  public static final int MODE_FAST_RISE = 3; // rapid price gains
  public static final int MODE_FAST_FALL = 4; // rapid price drops
  public static final int MODE_CHAOTIC = 5; // high volatility, unpredictable swings

  private final Random random = new Random();

  // Feature: Chaos modifier
  // A 0.0–1.0 multiplier that amplifies chaos and volatility across all stocks.
  // Set to 0.0 (default) to leave it inert.
  private double chaosModifier = 0.0;

  @Override
  public Stock createStock(String symbol, String company, ArrayList<BigDecimal> priceHistory) {
    return new MarketSimStock(symbol, company, priceHistory); // own type
  }

  @Override
  public String getStrategyId() {
    return "MARKET_SIMULATOR";
  }

  /**
   * When saving returns a extra line with the extra attributes for {@link MarketSimStock}
   *
   * @param stock stock
   * @return line with the extra attributes
   */
  @Override
  public String saveStockExtras(Stock stock) {
    MarketSimStock stockExtra = (MarketSimStock) stock;
    return stockExtra.getD()
        + "|"
        + stockExtra.getMode()
        + "|"
        + stockExtra.getDur()
        + "|"
        + stockExtra.getRestingVal();
  }

  /**
   * Parses extras to add to strategy specific stock
   *
   * @param stock
   * @param extras
   */
  @Override
  public void deserializeStockExtras(Stock stock, String extras) {
    MarketSimStock s = (MarketSimStock) stock;
    String[] parts = extras.split("\\|");
    s.setD(Double.parseDouble(parts[0]));
    s.setMode(Integer.parseInt(parts[1]));
    s.setDur(Integer.parseInt(parts[2]));
    s.setRestingVal(Integer.parseInt(parts[3]));
  }

  @Override
  public void copyStockState(Stock source, Stock target) {
    if (source instanceof MarketSimStock src && target instanceof MarketSimStock tgt) {
      tgt.setD(src.getD());
      tgt.setMode(src.getMode());
      tgt.setDur(src.getDur());
      tgt.setRestingVal(src.getRestingVal());
    }
  }

  /**
   * Advances all stocks by one calculateNewPrice. Equivalent to M.calculateNewPrice() in
   * minigameMarket.js.
   */
  @Override
  public void calculateNewPrice(List<Stock> stocks) {
    // Feature: Global Market Event
    // Roughly 1 calculateNewPrice in 20 triggers a correlated shock across the market (crash or
    // boom).
    // globP is the per-stock probability of being hit when a shock occurs.
    double globD = 0.0;
    if (random.nextDouble() < 0.05 + 0.08 * chaosModifier) {
      globD = (random.nextDouble() - 0.5) * 2.0;
    }

    for (Stock stock : stocks) {
      MarketSimStock me = (MarketSimStock) stock;
      double val = me.getSalesPrice().doubleValue();
      double d = me.getD();
      int mode = me.getMode();
      int dur = me.getDur();
      int restingVal = me.getRestingVal();

      // 2% chance that the price halts for a week.
      if (random.nextDouble() < 0.02 + 0.03 * chaosModifier) {
        val += (random.nextDouble() - 0.5) * 20;
        me.addNewSalesPrice(
            BigDecimal.valueOf(Math.max(val, 1.0)).setScale(2, RoundingMode.HALF_UP));
        continue;
      }

      // Feature: Momentum Decay
      // Reduces momentum slightly every calculateNewPrice so it doesn't compound indefinitely.
      d *= 0.97 + 0.01 * chaosModifier;

      // Feature: Mode-Based Momentum
      // Each mode biases d (and sometimes val directly) in a characteristic direction.
      switch (mode) {
        case MODE_STABLE -> {
          d *= 0.75;
          d += 0.04 * (random.nextDouble() - 0.5);
        }
        case MODE_SLOW_RISE -> {
          d *= 0.80;
          d += 0.07 * (random.nextDouble() - 0.1);
        }
        case MODE_SLOW_FALL -> {
          d *= 0.80;
          d -= 0.07 * (random.nextDouble() - 0.1);
        }
        case MODE_FAST_RISE -> {
          d += 0.15 * (random.nextDouble() - 0.1);
          val += random.nextDouble() * 30;
        }
        case MODE_FAST_FALL -> {
          d -= 0.15 * (random.nextDouble() - 0.1);
          val -= random.nextDouble() * 30;
        }
        case MODE_CHAOTIC -> d += 0.50 * (random.nextDouble() - 0.5);
      }

      // Feature: Mean Reversion
      // Nudges val 1% of the way toward restingVal each calculateNewPrice.
      val += (restingVal - val) * 0.01;

      // Feature: Global Shock Impact
      // Applies the market-wide shock (computed above) to this individual stock.
      if (globD != 0.0 && random.nextDouble() < 0.4) {
        val -= (1 + d * Math.pow(random.nextDouble(), 3) * 10) * globD;
        val -= globD * (1 + Math.pow(random.nextDouble(), 3) * 20);
        d += globD * (1 + random.nextDouble() * (3 + chaosModifier));
        dur = 0; // forces an immediate mode re-roll this calculateNewPrice
      }

      // Feature: Rare Large Spike
      // The 11th-power distribution produces very small noise almost always,
      // but occasionally a large jump.
      val += Math.pow((random.nextDouble() - 0.5) * 2, 11) * 20;

      // Feature: Constant Small Noise
      d += 0.1 * (random.nextDouble() - 0.5) + 0.1 * (random.nextDouble() - 0.5) * chaosModifier;
      if (random.nextDouble() < 0.15) val += (random.nextDouble() - 0.5) * 30;

      // Feature: Rare Large Jump
      // 3% chance of a significant price move each calculateNewPrice.
      if (random.nextDouble() < 0.03) {
        val += (random.nextDouble() - 0.5) * (50 + 50 * chaosModifier);
      }

      // Feature: Random Momentum Kick
      // 10% chance of a sudden shift in momentum.
      if (random.nextDouble() < 0.1) {
        d += (random.nextDouble() - 0.5) * (0.3 + 0.2 * chaosModifier);
      }

      // Feature: Chaotic Mode Extra Noise
      if (mode == MODE_CHAOTIC) {
        if (random.nextDouble() < 0.5) val += (random.nextDouble() - 0.5) * 30;
        if (random.nextDouble() < 0.05) d = (random.nextDouble() - 0.5) * (2 + chaosModifier);
      }

      // Feature: Extra Noise in fast-rise and fast-fall modes.
      if (mode == MODE_FAST_RISE && random.nextDouble() < 0.3) {
        d += (random.nextDouble() - 0.5) * 0.1;
        val += (random.nextDouble() - 0.7) * 30;
      }
      if (mode == MODE_FAST_FALL && random.nextDouble() < 0.3) {
        d += (random.nextDouble() - 0.5) * 0.1;
        val += (random.nextDouble() - 0.3) * 30;
      }

      // Feature: Fast rise-to-fall Transition
      // A 3% per-calculateNewPrice chance that a fast rise market tips into a fast fall market.
      if (mode == MODE_FAST_RISE && random.nextDouble() < 0.04 * d) {
        mode = MODE_FAST_FALL;
        d *= 0.4;
        dur = 6;
      }

      // Feature: High-Price Momentum Cap
      if (val > 1000 && d > 0) d *= 0.9;

      // Apply accumulated momentum to price.
      val += d * val * 0.1;

      // Feature: Low-Price Soft Floor
      if (val < 5) val += (5 - val) * 0.5;
      if (val < 5 && d < 0) d *= 0.95;

      // Feature: Hard Price Floor
      val = Math.max(val, 1.0);

      // Feature: Mode Duration + Re-roll
      dur--;
      if (dur <= 0) {
        dur = 4 + random.nextInt(6); // 4–10 ticks
        if (mode != MODE_CHAOTIC
            && random.nextDouble() < chaosModifier
            && random.nextDouble() < 0.15) {
          mode = MODE_CHAOTIC;
        } else {
          int newMode = mode;
          while (newMode == mode) {
            newMode = randomMode();
          }
          mode = newMode;
          d *= 0.5;
        }
      }

      me.addNewSalesPrice(BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP));
      me.setMode(mode);
      me.setDur(dur);
      me.setD(d);
    }
  }

  private int randomMode() {
    int[] pool = {
      MODE_STABLE, MODE_SLOW_RISE, MODE_SLOW_FALL, MODE_FAST_RISE, MODE_FAST_FALL, MODE_CHAOTIC
    };
    return pool[random.nextInt(pool.length)];
  }

  @Override
  public void setDifficulty(Difficulty difficulty) {
    double boost =
        switch (difficulty) {
          case EASY -> 0.0;
          case MEDIUM -> 0.3;
          case HARD -> 0.7;
          case REALISTIC -> 1.0;
        };
    this.chaosModifier = Math.max(0.0, Math.min(1.0, boost));
  }

  public double getChaosModifier() {
    return chaosModifier;
  }
}
