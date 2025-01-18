package it.unipi.wined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Order;
import it.unipi.wined.bean.Order_Insert;
import org.bson.Document;

import it.unipi.wined.driver.Mongo;
//import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.neo4j.Neo4JUtils;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import it.unipi.wined.spring.Actions;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.UUID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Scanner;



@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }) 
public class WinedApplication {
     
    //FUNZIONE UTILE (ANCHE PER IL CLIENT) PER INSERIRE NUOVI ORDINI
    public static LocalDate getRandomFutureDate() {
        Random random = new Random();
        int daysToAdd = random.nextInt(365); 
        return LocalDate.now().plus(daysToAdd, ChronoUnit.DAYS);
    }


	public static void main(String[] args) {
                       Scanner scanner = new Scanner(System.in);
        
        
        // Pulisci il buffer iniziale in caso di input residuo
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (true) {
            System.out.println("Inserisci il nickname o digita 'esc' per uscire:");
            String nickname = scanner.nextLine().trim(); // Usa trim() per evitare problemi di spazi

            if (nickname.equalsIgnoreCase("esc")) {
                break;
            }

            Order order = new Order();
            order.setIdOrder(UUID.randomUUID().toString());
            order.setConfirmationDate(LocalDate.now().toString());
            order.setDeliveryDate(getRandomFutureDate().toString());
            order.setOrderTotalCost(0);

            boolean moreWines = true;
            while (moreWines) {
                System.out.println("Inserisci il nome del vino:");
                String wineName = scanner.nextLine();
                System.out.println("Inserisci il nome della cantina:");
                String wineryName = scanner.nextLine();

                OrderList item = new OrderList();
                Order_Insert res = Mongo.retrieveIdAndPrice(wineName, wineryName);
                item.setWine_id(res.getWineId());
                item.setWine_name(wineName);
                item.setPrice(res.getPrice());

                System.out.println("Inserisci il numero di bottiglie:");
                int wineNumber = Integer.parseInt(scanner.nextLine().trim());
                item.setWine_number(wineNumber);

                order.setOrderElements(item); // Assicurati che il metodo per aggiungere elementi all'ordine sia corretto
                order.setOrderTotalCost(order.getOrderTotalCost() + (item.getPrice() * wineNumber));

                System.out.println("Vuoi aggiungere un altro vino? (sì/no)");
                String answer = scanner.nextLine().trim();
                if (!answer.equalsIgnoreCase("sì")) {
                    moreWines = false;
                }
            }

            boolean result = Mongo.addWineOrder(nickname, order);
            if (result) {
                System.out.println("Ordine aggiunto correttamente!");
            } else {
                System.out.println("Problemi nell'aggiunta dell'ordine");
            }
        }

        scanner.close();
	}

}
