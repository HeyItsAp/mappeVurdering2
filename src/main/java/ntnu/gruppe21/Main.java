package ntnu.gruppe21;

import ntnu.gruppe21.filehandler.Filehandler;

import java.io.File;

public class Main {
  public static void main(String[] args) {

    System.out.println("If you read this, you are gay");

    Exchange exchange = Filehandler.getExhangeData();
    System.out.println(exchange.getName() + ". Week: " + exchange.getWeek() + ", " + exchange.getStock("NVDA").getSymbol() + ": " + exchange.getStock("NVDA").getSalesPrice());

    Exchange exchange1 = Filehandler.getSaveData(Filehandler.saveExchangeData(exchange));
    System.out.println(exchange1.getName() + ". Week: " + exchange1.getWeek() + ", " + exchange1.getStock("NVDA").getSymbol() + ": " + exchange1.getStock("NVDA").getSalesPrice());

  }
}
