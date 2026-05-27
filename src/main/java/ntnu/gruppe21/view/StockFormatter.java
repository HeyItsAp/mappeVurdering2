package ntnu.gruppe21.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ntnu.gruppe21.model.Stock;

/** Shared formatting utilities for displaying stock prices and changes in the view layer. */
public final class StockFormatter {

  private StockFormatter() {}

  /**
   * Formats a {@link BigDecimal} value to two decimal places.
   *
   * @param v the value to format
   * @return a string like {@code "123.45"}
   */
  public static String fmt(BigDecimal v) {
    return String.format("%.2f", v);
  }

  /**
   * Formats the latest weekly price change for a stock as a signed percentage string.
   *
   * @param s the stock whose change to format
   * @return a string like {@code "+1.23%"} or {@code "-0.45%"}
   */
  public static String fmtChange(Stock s) {
    BigDecimal change = s.getLatestPriceChange();
    BigDecimal prev = s.getSalesPrice().subtract(change);
    if (prev.signum() == 0) return "0.00%";
    BigDecimal pct = change.divide(prev, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    String sign = pct.signum() >= 0 ? "+" : "";
    return sign + String.format("%.2f", pct) + "%";
  }
}
