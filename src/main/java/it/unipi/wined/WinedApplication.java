package it.unipi.wined;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import it.unipi.wined.bean.Order;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.config.Driver_Config;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WinedApplication {

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--n4j-conn":
                    if (i + 1 < args.length) {
                        Neo4JUtils.connectionString = args[++i]; // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --n4j-conn flag requires a value");
                        return;
                    }
                    break;
                case "--n4j-user":
                    if (i + 1 < args.length) {
                        Neo4JUtils.neo4j_user = args[++i]; // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --n4j-user flag requires a value");
                        return;
                    }
                    break;
                case "--n4j-password":
                    if (i + 1 < args.length) {
                        Neo4JUtils.neo4j_user = args[++i]; // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --n4j-password flag requires a value");
                        return;
                    }
                    break;
                case "--n4j-db":
                    if (i + 1 < args.length) {
                        Neo4JUtils.db = args[++i]; // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --n4j-db flag requires a value");
                        return;
                    }
                    break;
                case "--mongo-db":
                    if (i + 1 < args.length) {
                        Driver_Config.setMONGO_DBNAME(args[++i]); // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --mongo-db flag requires a value");
                        return;
                    }
                    break;
                case "--mongo-conn":
                    if (i + 1 < args.length) {
                        Driver_Config.setMONGO_CONNECTION_STRING(args[++i]); // Capture the next argument as the value
                    } else {
                        System.err.println("Error: --mongo-conn flag requires a value");
                        return;
                    }
                    break;
                default:
                    System.err.println("Invalid flag: " + args[i]);
            }
        }
        System.out.println("Ciao");
        System.out.println("Connection Parameters");
        System.out.println("# Neo4j driver : " + Neo4JUtils.connectionString + " | db: " + Neo4JUtils.db + " | user: " + Neo4JUtils.neo4j_user + " | pw: " + Neo4JUtils.neo4j_password);
        System.out.println("# MongoDB driver : " + Driver_Config.getMongoConnectionString() + " | db: " + Driver_Config.getMongoDbName());

        SpringApplication.run(WinedApplication.class, args);

    }

}
