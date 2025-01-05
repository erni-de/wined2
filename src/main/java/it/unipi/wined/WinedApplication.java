package it.unipi.wined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }) 
public class WinedApplication {

	public static void main(String[] args) {
                System.out.println("Ciao");
		SpringApplication.run(WinedApplication.class, args);
	}

}
