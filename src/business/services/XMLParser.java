package business.services;

import business.models.Fuel;
import business.models.PetrolStation;
import business.models.PetrolStations;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class XMLParser {

    private XMLParser() { }

    public static PetrolStations parsePetrolStationsXML(){
        List<PetrolStation> petrolStations = new ArrayList<>();
        LocalDate date = null;
        PetrolStation petrolStation;
        try {
            Document document = getDocument();
            NodeList nodeList = getNodeList(document);
            date = getDate(document);

            //This part goes through the node list and sets PetrolStation instances to proper values based
            //on the XML element.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    petrolStation = getPopulatedPetrolStation(element);

                    //Fuels attribute needs more complicated logic since it is a List, not a single element.
                    NodeList fuelNodeList = element.getElementsByTagName("fuel");
                    petrolStation.setFuels(makeFuelsList(fuelNodeList));
                    petrolStations.add(petrolStation);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return new PetrolStations(petrolStations, date);
    }

    private static NodeList getNodeList(Document document) {
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("petrolStation");
    }

    private static LocalDate getDate(Document document) {
        Element root = document.getDocumentElement();
        return LocalDate.parse(root.getAttribute("date"));
    }

    //Returns a petrol station with simple elements populated
    private static PetrolStation getPopulatedPetrolStation(Element element) {
        PetrolStation petrolStation = new PetrolStation();
        petrolStation.setName(element.getAttribute("name"));
        petrolStation.setAddress(element.getAttribute("address"));
        petrolStation.setCity(element.getAttribute("city"));
        return petrolStation;
    }

    private static Document getDocument() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new File("resources/PetrolStations.xml"));
    }

    //Converts a list of nodes into a list of fuels and returns that list.
    private static List<Fuel> makeFuelsList(NodeList fuelNodes){
        if(fuelNodes==null) return new ArrayList<>();

        List<Fuel> fuelList= new ArrayList<>();
        Fuel fuel = null;
        for (int i = 0; i < fuelNodes.getLength(); i++) {
            Node fuelNode = fuelNodes.item(i);
            if(fuelNode.getNodeType() == Node.ELEMENT_NODE){
                Element fuelElement = (Element) fuelNode;
                fuel = new Fuel(fuelElement.getAttribute("type"), getPrice(fuelElement));
                fuelList.add(fuel);
            }
        }
        return fuelList;
    }

    private static double getPrice(Element fuelElement){
        String priceString = fuelElement.getElementsByTagName("price").item(0).getTextContent();
        return Double.parseDouble(priceString.charAt(0) == '$' ? priceString.substring(1) : priceString);
    }
}
