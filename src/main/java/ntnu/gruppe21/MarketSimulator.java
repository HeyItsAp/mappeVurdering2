package ntnu.gruppe21;

import java.util.List;
import java.util.Random;

/**
 * Java translation of the Cookie Clicker bank minigame's stock price advancement logic (M.tick).
 */
public class MarketSimulator {

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

  // ---- Per-stock simulation state ----
  // Create one Good per stock. val/d/mode/dur are the four core fields the simulator needs;
  // restingVal anchors mean reversion.
  public static class Good {
    public double val; // current price
    public double d; // momentum — added to val each tick after all adjustments
    public int mode; // active market mode (see MODE_* constants above)
    public int dur; // ticks remaining in the current mode before re-rolling
    public int restingVal; // the "natural" price this stock slowly drifts back toward

    public Good(double val, double d, int mode, int dur, int restingVal) {
      this.val = val;
      this.d = d;
      this.mode = mode;
      this.dur = dur;
      this.restingVal = restingVal;
    }
  }

  /** Advances all goods by one tick. Equivalent to M.tick() in minigameMarket.js. */
  public void tick(List<Good> goods) {

    // Feature: Global Market Event
    // Roughly 1 tick in 20 triggers a correlated shock across the market (crash or boom).
    // globP is the per-stock probability of being hit when a shock occurs.
    double globD = 0.0;
    if (random.nextDouble() < 0.05 + 0.3 * chaosModifier) {
      globD = (random.nextDouble() - 0.5) * 2.0;
    }

    for (Good me : goods) {

      // 2% chance that the price halts for a week.
      if (random.nextDouble() < 0.02 + 0.03 * chaosModifier) {
        me.val += (random.nextDouble() - 0.5) * 20;
        System.out.println("pause");
        continue;
      }

      // Feature: Momentum Decay
      // Reduces momentum slightly every tick so it doesn't compound indefinitely.
      // Remove to make momentum fully persistent between ticks.
      me.d *= 0.97 + 0.01 * chaosModifier;

      // Feature: Mode-Based Momentum
      // Each mode biases d (and sometimes val directly) in a characteristic direction.
      switch (me.mode) {
        case MODE_STABLE -> {
          me.d *= 0.75;
          me.d += 0.04 * (random.nextDouble() - 0.5);
        }
        case MODE_SLOW_RISE -> {
          me.d *= 0.80;
          me.d += 0.07 * (random.nextDouble() - 0.1);
        }
        case MODE_SLOW_FALL -> {
          me.d *= 0.80;
          me.d -= 0.07 * (random.nextDouble() - 0.1);
        }
        case MODE_FAST_RISE -> {
          me.d += 0.15 * (random.nextDouble() - 0.1);
          me.val += random.nextDouble() * 30;
        }
        case MODE_FAST_FALL -> {
          me.d -= 0.15 * (random.nextDouble() - 0.1);
          me.val -= random.nextDouble() * 30;
        }
        case MODE_CHAOTIC -> me.d += 0.50 * (random.nextDouble() - 0.5);
      }

      // Feature: Mean Reversion
      // Nudges val 1% of the way toward restingVal each tick.
      me.val += (me.restingVal - me.val) * 0.01;

      // Feature: Global Shock Impact
      // Applies the market-wide shock (computed above) to this individual stock.
      // Works in concert with "Feature: Global Market Event".
      if (globD != 0.0 && random.nextDouble() < 0.4) {
        System.out.println("IMPACT!");
        me.val -= (1 + me.d * Math.pow(random.nextDouble(), 3) * 10) * globD;
        me.val -= globD * (1 + Math.pow(random.nextDouble(), 3) * 20);
        me.d += globD * (1 + random.nextDouble() * 4);
        me.dur = 0; // forces an immediate mode re-roll this tick
      }

      // Feature: Rare Large Spike
      // The 11th-power distribution produces very small noise almost always,
      // but occasionally a large jump.
      me.val += Math.pow((random.nextDouble() - 0.5) * 2, 11) * 20;

      // Feature: Constant Small Noise
      // Adds a small random kick to momentum and a 15% chance of a tiny price bump.
      me.d += 0.1 * (random.nextDouble() - 0.5) + 0.1 * (random.nextDouble() - 0.5) * chaosModifier;
      if (random.nextDouble() < 0.15) me.val += (random.nextDouble() - 0.5) * 30;

      // Feature: Rare Large Jump
      // 3% chance of a significant price move each tick.
      // Chaos modifier increases the magnitude.
      if (random.nextDouble() < 0.03) {
        System.out.println("kabomba");
        me.val += (random.nextDouble() - 0.5) * (50 + 50 * chaosModifier);
      }

      // Feature: Random Momentum Kick
      // 10% chance of a sudden shift in momentum.
      // Remove to make momentum evolve more smoothly.
      if (random.nextDouble() < 0.1) {
        me.d += (random.nextDouble() - 0.5) * (0.3 + 0.2 * chaosModifier);
      }

      // Feature: Chaotic Mode Extra Noise
      // Adds large additional randomness exclusively in chaotic mode.
      if (me.mode == MODE_CHAOTIC) {
        if (random.nextDouble() < 0.5) me.val += (random.nextDouble() - 0.5) * 30;
        if (random.nextDouble() < 0.3) me.d = (random.nextDouble() - 0.5) * (2 + 6 * chaosModifier);
      }

      // Feature: Extra Noise
      // Adds noise within fast-rise and fast-fall modes.
      if (me.mode == MODE_FAST_RISE && random.nextDouble() < 0.3) {
        me.d += (random.nextDouble() - 0.5) * 0.1;
        me.val += (random.nextDouble() - 0.7) * 30;
      }
      if (me.mode == MODE_FAST_FALL && random.nextDouble() < 0.3) {
        me.d += (random.nextDouble() - 0.5) * 0.1;
        me.val += (random.nextDouble() - 0.3) * 30;
      }

      // Feature: Fast rise-to-fall Transition
      // A 3% per-tick chance that a fast rise market tips into a fast fall market.
      if (me.mode == MODE_FAST_RISE && random.nextDouble() < 0.03) {
        System.out.println("sike");
        me.mode = MODE_FAST_FALL;
        me.d *= 0.4;
        me.dur = 6;
      }

      // Feature: High-Price Momentum Cap
      // Dampens upward momentum when val exceeds 100, preventing runaway prices.
      if (me.val > 2000 && me.d > 0) me.d *= 0.9;

      // Apply accumulated momentum to price.
      me.val += me.d * me.val * 0.1;

      // Feature: Low-Price Soft Floor
      // Gently pushes prices back up when they fall below 5, and damps negative
      // momentum below 5 to slow the descent. Remove to allow prices to fall to 1.
      if (me.val < 5) me.val += (5 - me.val) * 0.5;
      if (me.val < 5 && me.d < 0) me.d *= 0.95;

      // Feature: Hard Price Floor
      // Prevents price from dropping below 1 under any circumstances.
      // Remove (with caution) to allow prices to reach zero or go negative.
      me.val = Math.max(me.val, 1.0);

      // Feature: Mode Duration + Re-roll
      // Counts down the mode duration. When it hits zero, a new mode is randomly chosen.
      // Remove to lock each stock into its initial mode indefinitely.
      me.dur--;
      if (me.dur <= 0) {
        me.dur = 4 + random.nextInt(6); // 4–10 ticks

        // Sub-feature: Chaos modifier Mode Bias
        // When chaosModifier is active, re-rolls are biased toward MODE_CHAOTIC.
        // Also, fast rise/fall markets have a 70% chance of becoming chaotic when they expire.
        if (random.nextDouble() < chaosModifier && random.nextDouble() < 0.15) {
          me.mode = MODE_CHAOTIC;
        } else {
          int newMode = me.mode;
          while (newMode == me.mode) {
            newMode = randomMode();
          }
          me.mode = newMode;
          me.d *= 0.5;
          System.out.println(me.mode);
        }
      }
    }
  }

  // Weighted random mode matching the original distribution:
  // stable×1, slow_rise×2, slow_fall×2, fast_rise×1, fast_fall×1, chaotic×1  (out of 8)
  private int randomMode() {
    int[] pool = {
      MODE_STABLE, MODE_SLOW_RISE, MODE_SLOW_FALL, MODE_FAST_RISE, MODE_FAST_FALL, MODE_CHAOTIC
    };
    return pool[random.nextInt(pool.length)];
  }

  public void setChaosModifier(double boost) {
    this.chaosModifier = Math.max(0.0, Math.min(1.0, boost));
  }

  public double getChaosModifier() {
    return chaosModifier;
  }
}
