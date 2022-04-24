package cs505finaltemplate.CEP;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cs505finaltemplate.Launcher;
import io.siddhi.core.util.transport.InMemoryBroker;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;

public class OutputSubscriber implements InMemoryBroker.Subscriber {

    private Gson gson;
    private String topic;
    public String[] alerts;
    final Type typeOfListMap = new TypeToken<List<Map<String,String>>>(){}.getType();

    public OutputSubscriber(String topic, String streamName) {
        gson = new Gson();
        this.topic = topic;
    }

    @Override
    public void onMessage(Object msg) {

        try {

            //Reset alert list.
            if (!Launcher.alerts.isEmpty())
                Launcher.alerts.clear();

            //You will need to parse output and do other logic,
            //but this sticks the last output value in main
            Launcher.beforeCEPOutput = Launcher.lastCEPOutput;
            Launcher.lastCEPOutput = String.valueOf(msg);

            Map<String,String> beforeDict = getZipMap(Launcher.beforeCEPOutput, false);
            Map<String,String> lastDict = getZipMap(Launcher.lastCEPOutput, true);

            for (String befKey : beforeDict.keySet()) {
                for (String lastKey : lastDict.keySet()) {
                    if (befKey.toString() == lastKey.toString()){
                        int b = Integer.parseInt(beforeDict.get(befKey));
                        int l = Integer.parseInt(lastDict.get(lastKey));
                        //System.out.println(b);
                        //System.out.println(l);
                        if (l >= b*2){
                            Launcher.alerts.add(befKey.toString());
                        }
                    }
                }
            }

            Launcher.state_alert = (Launcher.alerts.size() > 4);

            System.out.println("PREV OUTPUT CEP EVENT: " + beforeDict.toString());
            System.out.println("OUTPUT CEP EVENT: " + lastDict.toString());
            System.out.println("ZIPS ON ALERT: " + Launcher.alerts.toString());
            System.out.println("STATE ALERT: " + Launcher.state_alert.toString());
            System.out.println("");
            

            //String[] sstr = String.valueOf(msg).split(":");
            //String[] outval = sstr[2].split("}");
            //System.out.println(outval);
            //Launcher.accessCount = Long.parseLong(outval[0]);

        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    private Map<String,String> getZipMap(String a, Boolean test){
        if (a.contains("[")){
            a = a.substring(1, a.length() - 1);
        }
        a = a.replace("{\"event\":{", "");
        a = a.replace("}", "");
        a = a.replace("\"zip_code\":", "");
        a = a.replace("\"count\":", "");
        a = a.replace("\"", "");
        //System.out.println(a);
        String[] sstr = a.split(",");

        Map<String,String> d = new HashMap<>();;
        for (int i = 0; i < sstr.length-1; i+=2) {
            d.put(sstr[i], sstr[i+1]);
        }

        /*
        if (test){
            d.put("11111","2");
            d.put("22222","2");
            d.put("33333","2");
            d.put("44444","2");
            d.put("55555","2");
        } else{
            d.put("11111","1");
            d.put("22222","1");
            d.put("33333","1");
            d.put("44444","1");
            d.put("55555","1");
        }
        */
        
        return d;
    }

    @Override
    public String getTopic() {
        return topic;
    }

}
