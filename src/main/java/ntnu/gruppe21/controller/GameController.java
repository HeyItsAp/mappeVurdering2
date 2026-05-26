package ntnu.gruppe21.controller;

import java.math.BigDecimal;
import ntnu.gruppe21.filehandler.SaveManager;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.transaction.Transaction;
import ntnu.gruppe21.model.transaction.TransactionException;

/**
 * Mediates between the model layer and the view layer during an active game session. Holds the
 * current {@link Player} and {@link Exchange}, and exposes all game actions that views may trigger.
 */
public class GameController {

  private final Player player;
  private final Exchange exchange;
  private final String saveSlot;

  /**
   * Creates a GameController for an active game session.
   *
   * @param player the player for this session
   * @param exchange the exchange for this session
   * @param saveSlot the save-folder name used when persisting the game
   */
  public GameController(Player player, Exchange exchange, String saveSlot) {
    this.player = player;
    this.exchange = exchange;
    this.saveSlot = saveSlot;
  }

  /** Returns the current player. */
  public Player getPlayer() {
    return player;
  }

  /** Returns the current exchange. */
  public Exchange getExchange() {
    return exchange;
  }

  /** Returns the save-slot name for this session. */
  public String getSaveSlot() {
    return saveSlot;
  }

  /**
   * Buys the given quantity of a stock and commits the transaction against the player.
   *
   * @param symbol the stock symbol to buy
   * @param quantity the number of shares to buy
   * @throws IllegalArgumentException if the symbol is unknown or funds are insufficient
   * @throws TransactionException if the transaction cannot be committed
   */
  public void buyStock(String symbol, BigDecimal quantity) throws TransactionException {
    Transaction purchase = exchange.buy(symbol, quantity, player);
    purchase.commit(player);
  }

  /**
   * Sells the given quantity of a stock and commits the transaction against the player.
   *
   * @param symbol the stock symbol to sell
   * @param quantity the number of shares to sell
   * @throws IllegalArgumentException if the symbol is unknown
   * @throws TransactionException if the transaction cannot be committed
   */
  public void sellStock(String symbol, BigDecimal quantity) throws TransactionException {
    Transaction sale = exchange.sell(symbol, quantity, player);
    sale.commit(player);
  }

  /** Advances the exchange by one week, updating all stock prices. */
  public void advanceWeek() {
    exchange.advance();
    player.getChallengeManager().evaluateChallenges(player);
  }

  /**
   * Saves the current game state to the save slot.
   *
   * @throws Exception if saving fails
   */
  public void saveGame() throws Exception {
    new SaveManager(saveSlot, false).save(player, exchange);
  }

  /**
   * Sells every share currently held in the player's portfolio. Intended for the "sell all and
   * quit" flow.
   *
   * @throws TransactionException if any individual sale cannot be committed
   */
  public void sellAll() throws TransactionException {
    for (ntnu.gruppe21.model.Share share :
        new java.util.ArrayList<>(player.getPortfolio().getShares())) {
      Transaction sale = exchange.sell(share.getStock().getSymbol(), share.getQuantity(), player);
      sale.commit(player);
    }
  }
}
