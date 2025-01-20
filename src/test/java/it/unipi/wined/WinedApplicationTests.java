package it.unipi.wined;

import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.connectionIp;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WinedApplicationTests {

  
    

}
