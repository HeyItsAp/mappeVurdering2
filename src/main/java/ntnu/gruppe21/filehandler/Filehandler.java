package ntnu.gruppe21.filehandler;

import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Filehandler {
    public static String saveExchangeData(Exchange exchange){
        String filename = "saveData" + exchange.getName() + exchange.getWeek();
        try (PrintWriter pw = new PrintWriter("src/main/resources/saves/" + filename + ".csv")){
            pw.println("# Save on Exchange: " + exchange.getName() + ", Week: " + exchange.getWeek());
            pw.println("# Ticker,Name,{Prices}");
            pw.println(" ");

            exchange.getStockMap().forEach((key, value) -> {
                String prices = value.getPriceHistory().stream()
                                .map(BigDecimal::toString)
                                .collect(Collectors.joining(";"));

                    pw.println(value.getSymbol() + "," + value.getCompany() + "," + prices);
                }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Attempt to save file: " + filename + ".csv; At resources/saves");
        return filename;
    }
    public static Exchange getExhangeData(){
        String csvfile = "src/main/resources/Exchanges/exchangeData.csv";
        String line = "";
        List<Stock> listOfStocks = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvfile));
            while ((line = br.readLine()) != null){
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()){
                    continue;
                }
                if(trimmedLine.startsWith("#")){
                    continue;
                }

                String[] values = trimmedLine.split(",");

                // Just a normal print out
                for (String value : values){
                    System.out.print(value.trim() + " ");
                }
                System.out.print("\n");

                Stock stock = new Stock(values[0], values[1], new BigDecimal(values[2]));
                listOfStocks.add(stock);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Exchange exhange = new Exchange("ExhangeFromFile", listOfStocks);
        return exhange;
    }
    public static Exchange getSaveData (String filename){
        String csvfile = "src/main/resources/saves/" + filename + ".csv";
        String line = "";
        List<Stock> listOfStocks = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvfile));
            while ((line = br.readLine()) != null){
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()){
                    continue;
                }
                if(trimmedLine.startsWith("#")){
                    continue;
                }

                String[] values = trimmedLine.split(",");

                // Just a normal print out
                for (String value : values){
                    System.out.print(value.trim() + " ");
                }
                System.out.print("\n");

                String[] savedPricesString = values[2].split(";");
                List<BigDecimal> savedPrices = Arrays
                        .stream(savedPricesString)
                        .map(BigDecimal::new)
                        .toList();
                Stock stock = new Stock(values[0], values[1], savedPrices.getFirst());
                for (int i = 1; i < savedPrices.size(); i++){
                    stock.addNewSalesPrice(savedPrices.get(i));
                }
                listOfStocks.add(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Exchange exhange = new Exchange("Save:" + filename, listOfStocks);
        return exhange;

    }
}
