package ntnu.gruppe21.filehandler;

import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Filehandler {
    public static void saveData(){

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
