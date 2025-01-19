package it.unipi.wined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WinedApplication {

    //FUNZIONE UTILE (ANCHE PER IL CLIENT) PER INSERIRE NUOVI ORDINI
    public static LocalDate getRandomFutureDate() {
        Random random = new Random();
        int daysToAdd = random.nextInt(365);
        return LocalDate.now().plus(daysToAdd, ChronoUnit.DAYS);
    }

    public static void main(String[] args) {

        System.out.println("Ciao");
        SpringApplication.run(WinedApplication.class, args);

    }

}
