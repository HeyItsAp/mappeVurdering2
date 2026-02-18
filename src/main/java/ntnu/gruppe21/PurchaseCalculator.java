package ntnu.gruppe21;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PurchaseCalculator implements TransactionCalculator{
    private final BigDecimal purchasePrice;
    private final BigDecimal quantity;

    public PurchaseCalculator(Share share) {
        purchasePrice = share.getPurchasePrice;
        quantity = share.getQuantity;
    }

    public BigDecimal calculateGross() {
        return purchasePrice.multiply(quantity);
    }

    public BigDecimal calculateCommission() {
        return calculateGross().divide(BigDecimal.valueOf(200),8, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTax() {
        return BigDecimal.valueOf(0);
    }

    public BigDecimal calculateTotal() {
        return calculateGross().add(calculateCommission()).add(calculateTax());
    }
}
