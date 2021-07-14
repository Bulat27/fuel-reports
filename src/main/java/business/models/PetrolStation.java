package main.java.business.models;

import java.util.List;

public class PetrolStation {

    private List<Fuel> fuels;
    private String name;
    private String address;
    private String city;

    public List<Fuel> getFuels() {
        return fuels;
    }

    public void setFuels(List<Fuel> fuels) {
        this.fuels = fuels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "name: " + name  +
                " address: " + address +
                " city:" + city + "\n" +
                printFuels();
    }

    private String printFuels(){
        String s = "";
        for (Fuel fuel: this.fuels) {
           s += fuel.toString() + "\n";
        }
        return s;
    }
}
