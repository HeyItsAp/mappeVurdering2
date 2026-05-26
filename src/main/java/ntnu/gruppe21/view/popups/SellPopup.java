package ntnu.gruppe21.view.popups;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.calculators.SaleCalculator;

public class SellPopup extends BuySellPopup {

  public SellPopup(Stock stock) {
    setTitle("Sell " + stock.getSymbol() + "?");
    setValue("at " + fmt(stock.getSalesPrice()) + " per share");
    setConfirm("Sell");
    updateCost(stock, BigDecimal.ONE);
    setOnQuantityChange(() -> updateCost(stock, getQuantity()));
  }

  private void updateCost(Stock stock, BigDecimal qty) {
    BigDecimal safeQty = qty.compareTo(BigDecimal.ZERO) > 0 ? qty : BigDecimal.ONE;
    Share share = new Share(stock, safeQty, stock.getSalesPrice());
    SaleCalculator calc = new SaleCalculator(share);
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
