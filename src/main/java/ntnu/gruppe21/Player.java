package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.Objects;

public class Player {
    /* Players name */
    private final String name;

    /* Starting money for the player */
    private final BigDecimal startingMoney;

    /* Current money for the player */
    private BigDecimal currentMoney;

    /* Players purchase portofolio */
    private Portfolio portfolio;

    /* Players transaction archive */
    private TransactionArchive transactionArchive;


    /**
     * Creates a new Player with the specified name and starting money.
     * 
     * @param name
     * @param startingMoney
     */
    public Player(String name, BigDecimal startingMoney) {
        this.name = name;
        this.startingMoney = startingMoney;
        this.currentMoney = startingMoney;
        this.portfolio = new Portfolio();
        this.transactionArchive = new TransactionArchive();
    }

    /**
     * Returns the name of the player.
     * 
     * @return the name in question.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the starting money of the player.
     * 
     * @return the starting money in question.
     */
    public BigDecimal getCurrecntMoney() {
        return currentMoney;
    }

    /**
     * Adds money to the player's current money.
     * 
     * @param amount The amount of money in question.
     */
    public void addMoney(BigDecimal amount) {
        this.currentMoney = this.currentMoney.add(Objects.requireNonNull(amount, "Amount cannot be null"));
    }

    /**
     * Subtracts/Withdraws money from the player's current money.
     * 
     * @param amount The amount of money in question.
     */
    public void withdrawMoney(BigDecimal amount) {
        this.currentMoney = this.currentMoney.subtract(Objects.requireNonNull(amount, "Amount cannot be null"));
    }

    /**
     * Returns the portfolio of the player.
     * 
     * @return portofolio of the player.
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Returns the transaction archive of the player.
     * 
     * @return Transaction archive/history of the player.
     */
    public TransactionArchive getTransactionArchive() {
        return transactionArchive;
    }
    
}
