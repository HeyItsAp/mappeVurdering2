package ntnu.gruppe21.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.calculators.PurchaseCalculator;

public class BuyPopup extends BuySellPopup {

  public BuyPopup(Stock stock) {
    setTitle("Buy " + stock.getSymbol() + "?");
    setValue("at " + fmt(stock.getSalesPrice()) + " per share");
    setConfirm("Buy");
    updateCost(stock, BigDecimal.ONE);
    setOnQuantityChange(() -> updateCost(stock, getQuantity()));
  }

  private void updateCost(Stock stock, BigDecimal qty) {
    BigDecimal safeQty = qty.compareTo(BigDecimal.ZERO) > 0 ? qty : BigDecimal.ONE;
    Share share = new Share(stock, safeQty, stock.getSalesPrice());
    PurchaseCalculator calc = new PurchaseCalculator(share);
    setPriceValue(fmt(stock.getSalesPrice()));
    setQuantityValue("× " + safeQty.setScale(0, RoundingMode.DOWN).toPlainString());
    setCommissionValue(fmt(calc.calculateCommission()));
    setTaxValue(fmt(calc.calculateTax()));
    setTotalValue(fmt(calc.calculateTotal()));
  }

  private static String fmt(BigDecimal v) {
    return String.format("%.2f", v);
  }
}
