package Business.Services;

import Business.Models.Fuel;
import Business.Models.PetrolStation;
import Business.Models.PetrolStationList;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public final class XMLParsingUtilities {

    //This is a Util class, so I've declared it as final and set its constructor to private in order to prevent
    //inheritance and instantiation.
    private XMLParsingUtilities() {
    }

    public static PetrolStationList parsePetrolStationListXML(){

        ArrayList<PetrolStation> petrolStations = new ArrayList<>();
        LocalDate date = null;
        PetrolStation petrolStation = null;

        try {

            //This part makes the list of PetrolStation nodes.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("AdditionalFiles/PetrolStations.xml"));
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("petrolStation");

            //This part gets the date attribute from the root element
            Element root = document.getDocumentElement();
            date = LocalDate.parse(root.getAttribute("date"));

            //This part goes through the node list and sets PetrolStation instances to proper values based
            //on the XML element.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    petrolStation = new PetrolStation();
                    petrolStation.setName(element.getAttribute("name"));
                    petrolStation.setAddress(element.getAttribute("address"));
                    petrolStation.setCity(element.getAttribute("city"));

                    //Fuels attribute needs more complicated logic since it is an ArrayList, not a single element.
                    NodeList fuelNodeList = element.getElementsByTagName("fuel");
                    ArrayList<Fuel> fuels = makeFuelsList(fuelNodeList);
                    petrolStation.setFuels(fuels);

                    petrolStations.add(petrolStation);

                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return new PetrolStationList(petrolStations, date);
    }

    //Converts a list of nodes into a list of fuels and returns that list.
    private static ArrayList<Fuel> makeFuelsList(NodeList fuelNodes){
        if(fuelNodes==null) return null;

        ArrayList<Fuel> fuelList= new ArrayList<>();
        Fuel fuel = null;
        for (int i = 0; i < fuelNodes.getLength(); i++) {
            Node fuelNode = fuelNodes.item(i);
            if(fuelNode.getNodeType() == Node.ELEMENT_NODE){
                Element fuelElement = (Element) fuelNode;
                fuel = new Fuel(fuelElement.getAttribute("type"), Double.parseDouble(fuelElement.getElementsByTagName("price").item(0).getTextContent()));
                fuelList.add(fuel);
            }
        }
        return fuelList;
    }

}
