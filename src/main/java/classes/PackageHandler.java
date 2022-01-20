package classes;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Set;

/*Т.к обмен данными по последовательному порту в библиотеке jssc реализован только посредством примитивных типов -
приводим все пакеты с данными от датчиков к виду int[].*/

public class PackageHandler {
    public static ArrayList<Integer> dataPackage = new ArrayList<>();
    public static ArrayList<Integer> dataFromSensor = new ArrayList<>();


    public static int[] sendRequest(int[] data) {
        int[] result = {};
        //проверяем разделители пакета
        if (data[0] == 0x7E && data[data.length - 1] == 0x7E) {
            switch (data[2]) {
                case 0x03:
                    result = pingSensor(data[3]);
                    break;
                case 0x0E:
                    result = pullVersion(data[3]);
                    break;
                case 0x01:
                    result = getValues(data[3]);
                    break;
                case 0x0B:
                    result = getFactoryId(data[3]);
                    break;
            }
        }
        return result;
    }


    //возвращает версию датчика
    public static int[] pullVersion(Integer address) {
        Set<Incl> sensors = Generator.getSensors();
        dataFromSensor.clear();
        int[] result = new int[10];
        String tmp;
        int requestType = 0x0E;
        for (Incl sensor : sensors) {
            if (sensor.getAddress().equals(Integer.toHexString(address))) {
                tmp = sensor.getVersion();
                for (int i = 0; i < tmp.length(); i++) {
                    dataFromSensor.add(Integer.valueOf(tmp.charAt(i)));  //преобразуем каждый символ в int-число
                }
                result = response(requestType, address, dataFromSensor);
                return result;
            }
        }
        return result;
    }

    //возвращает 0x35 в ответ на ping-запросы
    public static int[] pingSensor(Integer address) {
        Set<Incl> sensors = Generator.getSensors();
        dataFromSensor.clear();
        int[] result = {};
        int requestType = 0x03;
        for (Incl sensor : sensors) {
            if (sensor.getAddress().equals(Integer.toHexString(address))) {
                dataFromSensor.add(0x35);
                result = response(requestType, address, dataFromSensor);
                return result;
            }
        }
        return result;
    }

    public static int[] getFactoryId(Integer address) {
        Set<Incl> sensors = Generator.getSensors();
        dataFromSensor.clear();
        //размер данных согласно протокола - 4 байта
        int[] result = new int[4];
        String binaryAddress;
        int requestType = 0x0B;
        for (Incl sensor : sensors) {
            if (sensor.getAddress().equals(Integer.toHexString(address))) {
                //конвертируем заводской номер датчика в бинарную строку
                binaryAddress = Integer.toString(Integer.parseInt(sensor.getFactoryID()), 2);
                ArrayList<Integer> convertAddress = Converter.binaryToInt(binaryAddress);
                for (int i = 0; i < convertAddress.size(); i++) {
                    result[i] = convertAddress.get(i);
                }
                for (int i : result) {
                    dataFromSensor.add(i);
                }
                result = response(requestType, address, dataFromSensor);
                return result;
            }
        }
        return result;
    }

    public static int[] getValues(Integer address) {
        Set<Incl> sensors = Generator.getSensors();
        dataFromSensor.clear();
        //размер данных согласно протокола - 6 байт
        int[] result = new int[6];
        int requestType = 0x01;
        int axisX;
        int axisY;
        String binaryX;
        String binaryY;
        StringBuilder binaryTotal = new StringBuilder();
        for (Incl sensor : sensors) {
            if (sensor.getAddress().equals(Integer.toHexString(address))) {
                axisX = sensor.getAxisX();
                axisY = sensor.getAxisY();
                //конвертируем показания датчика в бинарную строку согласно документации к протоколу
                binaryX = Converter.convertValues(axisX);
                binaryY = Converter.convertValues(axisY);
                binaryTotal.append(binaryX);
                binaryTotal.append(binaryY);
                ArrayList<Integer> convertValues = Converter.binaryToInt(binaryTotal.toString());
                for (int i = 0; i < convertValues.size(); i++) {
                    result[i] = convertValues.get(i);
                }
                for (int i : result) {
                    dataFromSensor.add(i);
                }
                result = response(requestType, address, dataFromSensor);
                return result;
            }
        }
        return result;
    }


    private static int[] response(int requestType, Integer address, ArrayList<Integer> data) {
        dataPackage.clear();
        switch (requestType) {
            case 0x03:
                dataPackage.add(0x35);  //ответ "35" от пингуемого измерителя
                break;
            case 0x0E:
                dataPackage = Converter.versionPackage(data, address);
                break;
            case 0x01:
                dataPackage = Converter.valuesPackage(data, address);
                break;
            case 0x0B:
                dataPackage = Converter.factoryIdPackage(data, address);
                break;
        }
        int[] result = dataPackage.stream().mapToInt(i -> i).toArray();  //конвертируем ArrayList<Integer> в массив
        return result;
    }

    //конвертирует 1 байт hex-формата String типа в ASCII-символ
    private static byte toHex(char[] c) {
        byte hexByte = 0;
        try {
            byte[] h1 = Hex.decodeHex(c);
            hexByte = h1[0];
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return hexByte;
    }


}
