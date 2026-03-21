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
    public static void saveExchangeData(Exchange exchange){
        try {
            PrintWriter pw = new PrintWriter("src/main/resources/Exchanges/saveData" + exchange.getName() + exchange.getWeek() + ".csv");
            pw.write("# Save on Exchange: " + exchange.getName() + ", Week: " + exchange.getWeek());
            pw.write("# Ticker,Name,{Prices}");
            pw.write(" ");

            exchange.getStockMap().forEach((key, value) -> {
                String prices = value.getPriceHistory().stream()
                                .map(BigDecimal::toString)
                                .collect(Collectors.joining(";"));

                    pw.write(value.getSymbol() + "," + value.getCompany() + "," + prices);
                }
            );


        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
