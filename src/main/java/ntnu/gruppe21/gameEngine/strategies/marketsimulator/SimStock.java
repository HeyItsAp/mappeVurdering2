package ntnu.gruppe21.gameEngine.strategies.marketsimulator;

import ntnu.gruppe21.Stock;

import java.math.BigDecimal;

public class SimStock extends Stock {
    private double d;
    private int mode;
    private int dur;
    private int restingVal;

    public SimStock(String symbol, String company, BigDecimal salesPrice) {
        super(symbol, company, salesPrice);
        this.restingVal = (int) Math.round(salesPrice.doubleValue());
        this.d = 0.0;
        this.mode = 0;
        this.dur = 5;
    }
    // ---- Simulation state accessors ----

    public double getD() { return d; }
    public void setD(double d) { this.d = d; }
    public int getMode() { return mode; }
    public void setMode(int mode) { this.mode = mode; }
    public int getDur() { return dur; }
    public void setDur(int dur) { this.dur = dur; }
    public int getRestingVal() { return restingVal; }
    public void setRestingVal(int restingVal) { this.restingVal = restingVal; }
}