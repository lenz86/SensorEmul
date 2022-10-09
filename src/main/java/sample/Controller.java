package sample;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Set;

import classes.Converter;
import classes.Data;
import classes.Generator;
import classes.Incl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<String> list1;

    @FXML
    private TextArea log;

    @FXML
    private TextField nameT;

    @FXML
    private TextField factoryT;

    @FXML
    private TextField addressT;

    @FXML
    private TextField protocolT;

    @FXML
    private TextField valX;

    @FXML
    private TextField valY;

    @FXML
    private Button addSensorBtn;

    @FXML
    private Button startBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Button clrSensorBtn;

    @FXML
    private Button readConfFromFileBtn;

    @FXML
    private Label parErrorText;

    private Set<Incl> sensors = null;
    private ObservableList list = FXCollections.observableArrayList();
    private int selectedIndex;
    private Generator generator;

    @FXML
    void addSensorClick(MouseEvent event) {
        String inclName = nameT.getText();
        String inclID = factoryT.getText();
        String inclVersion = protocolT.getText();
        String inclAddress = Converter.convertHex(addressT.getText());
        addSensors(inclName, inclID, inclVersion, inclAddress);
    }

    void addSensors (String inclName, String inclID, String inclVersion, String inclAddress) {
        if (inclName.equals("") || inclID.equals("") || inclVersion.equals("") || inclAddress.equals("")) {
            parErrorText.setVisible(true);
        } else {
            Incl incl = new Incl(inclName, inclID, inclVersion, inclAddress);
            list.clear();
            sensors.add(incl);
            parErrorText.setVisible(false);
            for (Incl sensor : sensors) {
                list.add(sensor.getName());
            }
            list1.setItems(list);
            log.appendText("Added inclinometr: " + incl.toString() + "\n");
            clearFields();
        }
    }

    @FXML
    void clearSensorClick(MouseEvent event) {
        list.clear();
        sensors.clear();
        list1.setItems(list);
    }

    @FXML
    void readFromConf(MouseEvent event) {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(Main.class.getResourceAsStream("/sensorpreload.xml"));
            // Проверяем документ на отсутствие двойных пробелов, неправильных тегов и т.д
            document.getDocumentElement().normalize();
            // Получаем корневой элемент
//            Node root = document.getDocumentElement();
            // Просматриваем все подэлементы корневого - т.е. книги
            NodeList sensorsList = document.getElementsByTagName("Sensor");
            for (int j = 0; j < sensorsList.getLength(); j++) {
                Node first = sensorsList.item(j);
                NodeList sensorParam = first.getChildNodes();
                Node current;
                String inclName = "";
                String inclID = "";
                String inclVersion = "";
                String inclAddress = "";
                for (int i = 0; i < sensorParam.getLength(); i++) {
                    current = sensorParam.item(i);
                    if (current.getNodeType() == Node.ELEMENT_NODE) {
                        switch (current.getNodeName()) {
                            case "Name":
                                inclName = current.getTextContent();
                                break;
                            case "FactoryID":
                                inclID = current.getTextContent();
                                break;
                            case "ProtocolVersion":
                                inclVersion = current.getTextContent();
                                break;
                            case "Address":
                                inclAddress = current.getTextContent();
                                break;
                            default:
                                System.out.println("No such element");
                                break;
                        }
                    }
                }
                addSensors(inclName, inclID, inclVersion, inclAddress);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void removeSensorClick(MouseEvent event) {

    }

    @FXML
    void selectedItem(MouseEvent event) {
        String incl = list1.getSelectionModel().getSelectedItem();
        for (Incl sensor : sensors) {
            if (sensor.getName().equals(incl)) {
                nameT.setText(sensor.getName());
                factoryT.setText(String.valueOf(sensor.getFactoryID()));
                addressT.setText(Converter.decodeHex(sensor.getAddress()));
                protocolT.setText(sensor.getVersion());
                valX.setText(String.valueOf(sensor.getAxisX()));
                valY.setText(String.valueOf(sensor.getAxisY()));
            }
        }
    }


    @FXML
    void startBtnClick(MouseEvent event) {
        startBtn.disableProperty().set(true);
        stopBtn.disableProperty().set(false);
        generator = new Generator(sensors);
        generator.start();
    }

    @FXML
    void stopBtnClick(MouseEvent event) {
        stopBtn.disableProperty().set(true);
        startBtn.disableProperty().set(false);
        generator.interrupt();
    }


    @FXML
    void initialize() {
//        LogField logField = new LogField();
//        logField.start();
//        list1.getItems().addAll(Data.getSensorName());
        sensors = new HashSet<>();
    }

    void clearFields() {
        nameT.setText("");
        factoryT.setText("");
        protocolT.setText("");
        addressT.setText("");
    }


//    class LogField extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                if (!Data.isEmpty()) {
//                    log.appendText(Data.getData());
//                }
//            }
//        }
//    }
}
