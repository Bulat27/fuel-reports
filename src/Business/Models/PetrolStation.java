package Business.Models;

import java.util.ArrayList;

public class PetrolStation {

    //I'm not sure if ArrayList is the best Collection the best option for Collection here.
    //I don't know which operations will be used most frequently. I might change the implementation of the collection.
    private ArrayList<Fuel> fuels;
    private String name;
    private String address;
    private String city;

    public PetrolStation() {
    }

    public PetrolStation(ArrayList fuels, String name, String address, String city) {
        this.fuels = fuels;
        this.name = name;
        this.address = address;
        this.city = city;
    }

    public ArrayList getFuels() {
        return fuels;
    }

    public void setFuels(ArrayList fuels) {
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
        String s ="";
        for (Fuel fuel: fuels) {
           s+= fuel.toString() + "\n";
        }
        return s;
    }
}
