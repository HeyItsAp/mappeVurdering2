package ntnu.gruppe21.view.popups;

import java.math.BigDecimal;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.calculators.SaleCalculator;

public class SellPopup extends BuySellPopup {

  public SellPopup(Stock stock, int ownedShares) {
    setTitle("Sell " + stock.getSymbol() + "?");
    setValue("at " + fmt(stock.getSalesPrice()) + " per share");
    setConfirm("Sell");
    setMaxQuantity(ownedShares);
    updateCost(stock, 1);
    setOnQuantityChange(() -> updateCost(stock, getQuantity()));
  }

  private void updateCost(Stock stock, int qty) {
    int safeQty = qty > 0 ? qty : 1;
    Share share = new Share(stock, safeQty, stock.getSalesPrice());
    SaleCalculator calc = new SaleCalculator(share);
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
