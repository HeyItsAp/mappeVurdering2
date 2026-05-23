package ntnu.gruppe21.gameEngine.strategies.marketsimulator;

import java.math.BigDecimal;
import java.util.ArrayList;

import ntnu.gruppe21.Stock;

public class MarketSimStock extends Stock {
  private double d;
  private int mode;
  private int dur;
  private int restingVal;

  public MarketSimStock(String symbol, String company, ArrayList<BigDecimal> priceHistory) {
    super(symbol, company, priceHistory);
    this.restingVal = (int) Math.round(priceHistory.getLast().doubleValue());
    this.d = 0.0;
    this.mode = 0;
    this.dur = 5;
  }

  // ---- Simulation state accessors ----

  public double getD() {
    return d;
  }

  public void setD(double d) {
    this.d = d;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public int getDur() {
    return dur;
  }

  public void setDur(int dur) {
    this.dur = dur;
  }

  public int getRestingVal() {
    return restingVal;
  }

  public void setRestingVal(int restingVal) {
    this.restingVal = restingVal;
  }
}
