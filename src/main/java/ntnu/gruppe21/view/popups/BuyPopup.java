package ntnu.gruppe21.view.popups;

import java.math.BigDecimal;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.calculators.PurchaseCalculator;

/**
 * Confirmation popup for buying shares. The cost breakdown updates live as the player changes the
 * quantity.
 */
public class BuyPopup extends BuySellPopup {

  /**
   * Creates a buy popup for the given stock.
   *
   * @param stock the stock being bought
   */
  public BuyPopup(Stock stock) {
    setTitle("Buy " + stock.getSymbol() + "?");
    setValue("at " + fmt(stock.getSalesPrice()) + " per share");
    setConfirm("Buy");
    updateCost(stock, 1);
    setOnQuantityChange(() -> updateCost(stock, getQuantity()));
  }

  private void updateCost(Stock stock, int qty) {
    int safeQty = qty > 0 ? qty : 1;
    Share share = new Share(stock, safeQty, stock.getSalesPrice());
    PurchaseCalculator calc = new PurchaseCalculator(share);
    setPriceValue(fmt(stock.getSalesPrice()));
    setQuantityValue("× " + safeQty);
    setCommissionValue(fmt(calc.calculateCommission()));
    setTaxValue(fmt(calc.calculateTax()));
    setTotalValue(fmt(calc.calculateTotal()));
  }

  private static String fmt(BigDecimal v) {
    return String.format("%.2f", v);
  }
}
