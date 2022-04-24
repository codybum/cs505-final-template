package cs505final;

import cs505final.CEP.CEPEngine;
import cs505final.Topics.TopicConnector;
import cs505final.graphDB.GraphDBEngine;
import cs505final.database.DBEngine;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class Launcher {

    public static GraphDBEngine graphDBEngine;
    public static String inputStreamName;
    public static DBEngine dbEngine;
    public static CEPEngine cepEngine;
    public static TopicConnector topicConnector;
    public static final int WEB_PORT = 8082;

    public static String beforeCEPOutput = "{}";
    public static String lastCEPOutput = "{}";
    public static List<String> alerts = new ArrayList<String>();
    public static Boolean state_alert = false;


    public static void main(String[] args) throws IOException {


        //starting DB/CEP init

        //READ CLASS COMMENTS BEFORE USING
        //graphDBEngine = new GraphDBEngine();

        dbEngine = new DBEngine();
        cepEngine = new CEPEngine();

        System.out.println("Starting CEP...");

        inputStreamName = "testInStream";
        String inputStreamAttributesString = "zip_code string";

        String outputStreamName = "testOutStream";
        String outputStreamAttributesString = "zip_code string, count long";

        //Grouped by zip code.
        String queryString = " " +
                "from testInStream#window.timeBatch(15 sec) " +
                "select zip_code, count(*) as count " +
                "group by zip_code " +
                "insert into testOutStream; ";

        cepEngine.createCEP(inputStreamName, outputStreamName, inputStreamAttributesString, outputStreamAttributesString, queryString);

        System.out.println("CEP Started...");
        //end DB/CEP Init

        //start message collector
        Map<String,String> message_config = new HashMap<>();
        message_config.put("hostname","128.163.202.50"); //Fill config for your team in
        message_config.put("username","student");
        message_config.put("password","student01");
        message_config.put("virtualhost","4");

        topicConnector = new TopicConnector(message_config);
        topicConnector.connect();
        //end message collector

        //Embedded HTTP initialization
        startServer();

        try {
            while (true) {
                Thread.sleep(15000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startServer() throws IOException {

        final ResourceConfig rc = new ResourceConfig()
        .packages("cs505final.httpcontrollers");

        System.out.println("Starting Web Server...");
        URI BASE_URI = UriBuilder.fromUri("http://0.0.0.0/").port(WEB_PORT).build();
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

        try {
            httpServer.start();
            System.out.println("Web Server Started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
