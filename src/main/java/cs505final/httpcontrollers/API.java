package cs505final.httpcontrollers;

import com.google.gson.Gson;
import cs505final.Launcher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Path("/api")
public class API {

    @Inject
    private javax.inject.Provider<org.glassfish.grizzly.http.server.Request> request;

    private Gson gson;

    public API() {
        gson = new Gson();
    }


    @GET
    @Path("/getteam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getteam() {
        String responseString = "{}";
        try {
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("team_name", "Chimkin");
            responseMap.put("Team_members_sids", "[912206804]");
            responseMap.put("app_status_code","1");

            responseString = gson.toJson(responseMap);
            System.out.println(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getlastcep")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccessCount(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //generate a response
            Map<String,String> responseMap = new HashMap<>();
            responseString = Launcher.lastCEPOutput;

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }
    
    @GET
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reset() {
        String responseString = "{}";
        try {
            
            String queryString = "DROP TABLE vax_list";

            System.out.println(queryString);
            Launcher.dbEngine.executeUpdate(queryString);

            queryString = "DROP TABLE hospital_list";

            System.out.println(queryString);
            Launcher.dbEngine.executeUpdate(queryString);

            queryString = "DROP TABLE patient_list";

            System.out.println(queryString);
            Launcher.dbEngine.executeUpdate(queryString);

            Launcher.dbEngine.initDB();


            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("reset_status_code", "1");

            responseString = gson.toJson(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();
            
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("reset_status_code", "0");

            responseString = gson.toJson(responseMap);

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }
    
    @GET
    @Path("/zipalertlist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response zipalertlist(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //generate a response
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("ziplist",Launcher.alerts.toString());
            responseString = gson.toJson(responseMap);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/alertlist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response alertlist() {
        String responseString = "{}";
        try {
            
            String s = "0";
            if (Launcher.state_alert)
                s = "1";
            
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("state_status", s);

            responseString = gson.toJson(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }


}
