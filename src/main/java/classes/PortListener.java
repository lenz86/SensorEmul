package classes;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


//Класс для "прослушки" COM-порта. Отрабатывает при появлении байт-данных в буффере.

public class PortListener extends Thread {
    private String portName;


    public PortListener(String portName) {
        this.portName = portName;
    }

    @Override
    public void run() {
        SerialPort serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            EventListener eventListener = new EventListener(serialPort);
            serialPort.addEventListener(eventListener, SerialPort.MASK_RXCHAR);
            while (!Thread.currentThread().isInterrupted()) {  //Если прерван поток - останавливаем EventListener и закрываем порт
            }
            serialPort.removeEventListener();
        } catch (SerialPortException e) {
            e.printStackTrace();
        } finally {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    private class EventListener implements SerialPortEventListener {
        private SerialPort serialPort;

        public EventListener(SerialPort serialPort) {
            this.serialPort = serialPort;
        }
        //слушаем порт
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            int[] transmitData;
            int[] receiveData;
            //проверяем наличие байт данных
            if (serialPortEvent.getEventValue() > 0) {
                try {
                    //записываем в переменную полученный пакет
                    transmitData = serialPort.readIntArray();
                    //отправляем пакет в обработчик пакетов
                    receiveData = PackageHandler.sendRequest(transmitData);

                    //если есть ответ от устройств - отправляем ответный пакет в порт
                    if (receiveData.length > 0) {
//                        for (int r : receiveData) {
//                            System.out.println(r);
//                        }
                        serialPort.writeIntArray(receiveData);
                    }
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

