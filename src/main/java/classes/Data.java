package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Data {

    private static Queue<String> data = new ConcurrentLinkedQueue<String>();
    private static Set<Incl> sensors = null;

    public static synchronized void setData (String sensors) {
        data.offer(sensors);
    }

    public static synchronized String getData() {
        String result = data.poll() + "\n";
        return result;
    }

    public static synchronized boolean isEmpty() {
        return data.isEmpty();
    }

    public static void setSensors(Set sensors) {
        Data.sensors = sensors;
    }

    public static synchronized Set<Incl> getSensors() {
        return sensors;
    }

    public static synchronized List<String> getSensorName() {
        List<String> result = new ArrayList<>();
        for (Incl sensor : sensors) {
            result.add(sensor.getName());
        }
        return result;
    }
}
