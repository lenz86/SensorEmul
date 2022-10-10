package classes;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Generator extends Thread {
    private static Set<Incl> sensors;
    private static Logger log = Logger.getLogger(Generator.class.getName());

    public Generator(Set<Incl> sensors) {
        this.sensors = sensors;
    }

    public static Set<Incl> getSensors() {
        return sensors;
    }

    @Override
    public void run() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.log(Level.WARNING, "UNCHECKED EXCEPTION!: ", e);
            }
        });
        PortListener portListener = new PortListener("COM4");
        portListener.start();  //открытие COM-порта и запуск прослушивания получения данных
        while (!Thread.currentThread().isInterrupted()) {
            for (Incl sensor : sensors) {
                int x = ThreadLocalRandom.current().nextInt(-100, -20);
                int y = ThreadLocalRandom.current().nextInt(100, 250);
                sensor.setAxisX(x);
                sensor.setAxisY(y);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                portListener.interrupt();
                Thread.currentThread().interrupt();
                log.log(Level.WARNING, "Exception: ", e);
            }
        }
    }
}
