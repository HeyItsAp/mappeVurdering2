/**
 * Represents a portfolio of shares owned by an investor. The portfolio allows for adding and
 * removing shares, as well as checking if a specific share is contained within the portfolio.
 *
 * @author Adrian
 */
package ntnu.gruppe21.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ntnu.gruppe21.model.transaction.calculators.SaleCalculator;

/**
 * Portfolio class contains a list shares and contain methods to interact. For example: Methods for
 * searching, filtering, checking and of course adding {@link Share}
 */
public class Portfolio {
  /* A list of shares owned by the investor. */
  private final List<Share> shares;

  /** Creates a new Portfolio with an empty list of shares. */
  public Portfolio() {
    this.shares = new ArrayList<>();
  }

  /**
   * Adds a share to the portfolio.
   *
   * @param share the share in question.
   */
  public void addShare(Share share) {
    shares.add(Objects.requireNonNull(share, "Share cannot be null"));
  }

  /**
   * Removes a share from the portfolio.
   *
   * @param share the share in question.
   */
  public void removeShare(Share share) {
    shares.remove(Objects.requireNonNull(share, "Share cannot be null"));
  }

  /**
   * Returns a list of shares owned by the investor.
   *
   * @return the shares in question.
   */
  public List<Share> getShares() {
    return shares;
  }

  /**
   * Checks if the portfolio contains a specific share.
   *
   * @param share the share in question.
   * @return true/false based on whether the share is contained.
   */
  public boolean containsShare(Share share) {
    return shares.contains(share);
  }

  /**
   * Calculates and returns the total sales price of all shares in the portfolio.
   *
   * @return The net worth of the shares in the portfolio.
   */
  public BigDecimal getNetWorth() {
    BigDecimal netWorth = BigDecimal.ZERO;

    for (Share share : shares) {
      SaleCalculator s = new SaleCalculator(share);
      netWorth = netWorth.add(s.calculateTotal()); // Maybe use calculateGross()??? Unsure.
    }

    return netWorth;
  }

  /**
   * Returns distinct stocks in portfolio.
   *
   * @return the length of the list of distinct list, which is the number of distinct weeks.
   */
  public int countDistinctStock() {
    List<String> distinctStockSymobolList =
        shares.stream().map(s -> s.getStock().getSymbol()).distinct().toList();
    return distinctStockSymobolList.size();
  }
}
