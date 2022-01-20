package classes;

import java.util.Objects;

public class Incl {
        private int axisX;
        private int axisY;
        private String name;
        private String factoryID;
        private String version;
//        private String portNumber;
        private String address;

    public Incl(String name, String factoryID, String version, String address) {
        this.name = name;
        this.factoryID = factoryID;
        this.version = version;
        this.address = address;
        this.address = address;
    }

    public int getAxisX() {
        return axisX;
    }

    public void setAxisX(int axisX) {
        this.axisX = axisX;
    }

    public int getAxisY() {
        return axisY;
    }

    public void setAxisY(int axisY) {
        this.axisY = axisY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactoryID() {
        return factoryID;
    }

    public void setFactoryID(String factoryID) {
        this.factoryID = factoryID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

//    public String getPortNumber() {
//        return portNumber;
//    }
//
//    public void setPortNumber(String portNumber) {
//        this.portNumber = portNumber;
//    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Incl incl = (Incl) o;
        return factoryID.equals(incl.factoryID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factoryID);
    }

    @Override
    public String toString() {
        return "Incl{" +
                "axisX=" + axisX +
                ", axisY=" + axisY +
                ", name='" + name + '\'' +
                ", factoryID='" + factoryID + '\'' +
                ", version='" + version + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
