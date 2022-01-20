package classes;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;

public class Converter {

    public static String convertHex(String data) {
        //промежуточно преобразуем String-число из текстовых полей в int-число
        int tmp = Integer.parseInt(data);
        //преобразуем int-число в String-число в hex-формате
        String result = Integer.toHexString(tmp);
        return result;
    }

    public static String decodeHex(String data) {
        //промежуточно преобразуем String-число hex-формата в int-число
        int tmp = Integer.parseInt(data, 16);
        //преобразуем int-число в String-число
        String result = String.valueOf(tmp);
        return result;
    }

    public static String convertValues(int value) {
        StringBuilder binaryValue = new StringBuilder();
        if (value < 0) {
            binaryValue.append(10);
        } else {
            binaryValue.append("00");
        }
        String val = Integer.toBinaryString(Math.abs(value));
        for (int i = val.length(); i < 14; i++) {
            binaryValue.append(0);
        }
        binaryValue.append(val);
        binaryValue.append("00000000");
        return binaryValue.toString();
    }

    //преобразуем каждый байт из бинарной строки в int-значение
    public static ArrayList<Integer> binaryToInt (String data) {
        ArrayList<Integer> result = new ArrayList<>();
        if ((data.length() % 8) == 0) {
            int firstBit;
            int lastBit;
            for (int i = 0; i < data.length() / 8; i++) {
                firstBit = data.length() - (8 * (i + 1));
                lastBit = data.length() - 8 * i;
                result.add(Integer.parseInt(data.substring(firstBit, lastBit), 2));
            }
        } else {
            int firstBit;
            int lastBit;
            for (int i = 0; i < (data.length() / 8); i++) {
                firstBit = data.length() % 8 + 8 * (data.length() / 8 - (i + 1));
                lastBit = data.length() - 8 * (i);
                result.add(Integer.parseInt(data.substring(firstBit, lastBit), 2));
            }
            firstBit = 0;
            lastBit = data.length() % 8;
            result.add(Integer.parseInt(data.substring(firstBit, lastBit), 2));
        }
        return result;
    }

    public static int getCheckSum (ArrayList<Integer> data) {
        int result = 0;
        for (int i = 1; i < data.size(); i++) {
            result ^= data.get(i);
        }
        return result;
    }


    //конструирует пакет ответа на команду запроса версии измерителя
    public static ArrayList<Integer> versionPackage (ArrayList<Integer> data, Integer address) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0x7E);  //разделитель пакетов
        result.add(0x9B);  //идентификатор протокола
        result.add(0x0E);  //тип запроса
        result.add(address);  //адрес измерителя
        for (Integer d : data) {  //данные от измерителя
            result.add(d);
        }
        Integer checkSum = getCheckSum(result);
        result.add(checkSum);  //контрольная сумма
        result.add(0x7E);  //разделитель пакетов
        return result;
    }
    //конструирует пакет ответа на команду запроса заводского номера измерителя
    public static ArrayList<Integer> factoryIdPackage (ArrayList<Integer> data, Integer address) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0x7E);  //разделитель пакетов
        result.add(0x9C);  //идентификатор протокола
        result.add(0x0B);  //тип запроса
        result.add(address);  //адрес измерителя
        for (Integer d : data) {  //данные от измерителя
            result.add(d);
        }
        Integer checkSum = getCheckSum(result);
        result.add(checkSum);  //контрольная сумма
        result.add(0x7E);  //разделитель пакетов
        return result;
    }
    //конструирует пакет ответа на команду запроса значений измерителя
    public static ArrayList<Integer> valuesPackage (ArrayList<Integer> data, Integer address) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0x7E);  //разделитель пакетов
        result.add(0x9B);  //идентификатор протокола
        result.add(0x01);  //тип запроса
        result.add(address);  //адрес измерителя
        for (Integer d : data) {  //данные от измерителя
            result.add(d);
        }
        Integer checkSum = getCheckSum(result);
        result.add(checkSum);  //контрольная сумма
        result.add(0x7E);  //разделитель пакетов
        return result;
    }



}
