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

    public static List<PetrolStations> returnPetrolStations(String localDirectoryPath) throws IOException, SAXException, ParserConfigurationException {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(localDirectoryPath).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(localDirectoryPath + "/" + file.getName()));
        }
        return petrolStations;
    }

    private static PetrolStations parsePetrolStationsXML(String docPath) throws ParserConfigurationException, IOException, SAXException {
        List<PetrolStation> petrolStations = new ArrayList<>();
        LocalDate date;
        PetrolStation petrolStation;

        Document document = getDocument(docPath);
        NodeList nodeList = getNodeList(document);
        date = getDate(document);

        //This part goes through the node list and sets PetrolStation instances to proper values based
        //on the XML element.
        for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    petrolStation = getPopulatedPetrolStation(element, date);

                    //Fuels attribute needs more complicated logic since it is a List, not a single element.
                    NodeList fuelNodeList = element.getElementsByTagName("fuel");
                    petrolStation.setFuels(makeFuelsList(fuelNodeList));
                    petrolStations.add(petrolStation);
                }
            }
        return new PetrolStations(petrolStations);
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
    private static PetrolStation getPopulatedPetrolStation(Element element, LocalDate date) {
        PetrolStation petrolStation = new PetrolStation();
        petrolStation.setName(element.getAttribute("name"));
        petrolStation.setAddress(element.getAttribute("address"));
        petrolStation.setCity(element.getAttribute("city"));
        petrolStation.setDate(date);
        return petrolStation;
    }

    private static Document getDocument(String docPath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(new File(docPath));
    }

    //Converts a list of nodes into a list of fuels and returns that list.
    private static List<Fuel> makeFuelsList(NodeList fuelNodes){
        if(fuelNodes==null) return new ArrayList<>();

        List<Fuel> fuelList= new ArrayList<>();
        Fuel fuel;
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
        return Double.parseDouble(priceString.startsWith("$") ? priceString.substring(1) : priceString);
    }
}
