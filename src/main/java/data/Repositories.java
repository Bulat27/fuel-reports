package data;

import business.models.Fuel;
import business.models.PetrolStation;
import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.xml.sax.SAXException;
import properties.PropertiesCache;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class Repositories {

    private static final String DB_URL;
    private static final String USER_NAME;
    private static final String PASSWORD;
    public static final String DB_NAME;

    static{
        PropertiesCache properties = PropertiesCache.getInstance();
        DB_URL = properties.getProperty("dbUrl");
        USER_NAME = properties.getProperty("dbUserName");
        PASSWORD = properties.getProperty("dbPassword");
        DB_NAME = "PETROL";
    }


    private Repositories(){}

    private static void writeIntoDataBase(List<PetrolStations> petrolStationsList) throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            Statement stmt = conn.createStatement();
        ){
            createDatabase(conn, stmt, DB_NAME);
            conn.setCatalog(DB_NAME.toLowerCase());
            createTables(conn);
            insertData(petrolStationsList, conn);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        //conn.setCatalog(DB_NAME.toLowerCase());
        try(Statement stmt = conn.createStatement();) {
            String fuelSQL = returnFuelSQL();
            String psSQL = returnPsSQL();
            String plSQL = returnPlSQL();

            createTable("FUELS", fuelSQL, stmt, conn);
            createTable("PETROL_STATIONS", psSQL, stmt, conn);
            createTable("PRICE_LIST", plSQL, stmt, conn);
        }
    }

    private static String returnFuelSQL(){
        return "CREATE TABLE FUELS(\n" +
                "\tid INT AUTO_INCREMENT,\n" +
                "    name VARCHAR(40) NOT NULL UNIQUE,\n" +
                "    PRIMARY KEY (id)\n" +
                ");";
    }

    private static String returnPsSQL(){
        return "CREATE TABLE PETROL_STATIONS(\n" +
                "\tid INT AUTO_INCREMENT,\n" +
                "    name VARCHAR(40) NOT NULL,\n" +
                "    adress VARCHAR(40) NOT NULL,\n" +
                "    city VARCHAR(40) NOT NULL,\n" +
                "    PRIMARY KEY (id)\n" +
                ");";
    }

    private static String returnPlSQL(){
        return "CREATE TABLE PRICE_LIST(\n" +
                "\tid INT AUTO_INCREMENT,\n" +
                "    petrol_station_id INT NOT NULL,\n" +
                "    fuel_id INT NOT NULL,\n" +
                "    price DECIMAL(7,2) NOT NULL,\n" +
                "    date DATE NOT NULL,\n" +
                "    PRIMARY KEY (id),\n" +
                "    FOREIGN KEY (petrol_station_id) REFERENCES PETROL_STATIONS (id),\n" +
                "    FOREIGN KEY (fuel_id) REFERENCES FUELS (id)\n" +
                ");";
    }

    private static void createTable(String tableName, String sql, Statement stmt, Connection conn) throws SQLException {
        if(tableExists(conn, tableName)) return;

        stmt.executeUpdate(sql);
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData dm = conn.getMetaData();
        try(ResultSet rs = dm.getTables(DB_NAME.toLowerCase(), null, tableName, new String[] {"TABLE"});) {
            return rs.next();
        }
    }

    private static void createDatabase(Connection conn, Statement stmt, String dbName) throws SQLException {

            if(databaseExists(conn,dbName)) return;

            String sql = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(sql);
    }

    private static boolean databaseExists(Connection conn, String dbName) throws SQLException {
        try(ResultSet rs = conn.getMetaData().getCatalogs();){
            while(rs.next()){
                String s = rs.getString(1);
                if(s.equals(dbName.toLowerCase())) return true;
            }
        }
        return false;
    }

    private static void insertData(List<PetrolStations> petrolStationsList, Connection conn) throws SQLException {
        insertIntoFuels(petrolStationsList, conn);
    }

    private static void insertIntoFuels(List<PetrolStations> petrolStationsList, Connection conn) throws SQLException {
        String sql = "INSERT INTO FUELS (name) VALUES (?)";
        ArrayList<String> insertedFuels = new ArrayList<>();
        try(PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            for (PetrolStations p : petrolStationsList) {
                for(PetrolStation petrolStation : p.getPetrolStationList()){
                    for(Fuel fuel : petrolStation.getFuels()){
                        if(!insertedFuels.contains(fuel.getType())){
                            preparedStatement.setString(1, fuel.getType());
                            preparedStatement.executeUpdate();
                            insertedFuels.add(fuel.getType());
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            SFTPDownloader.downloadFiles();
            writeIntoDataBase(returnPetrolStations());
        } catch (SQLException | IOException | SAXException | ParserConfigurationException | JSchException | SftpException e) {
            e.printStackTrace();
        }
    }
//     TODO:THIS IS ONLY FOR TESTING
    private static List<PetrolStations> returnPetrolStations() throws IOException, SAXException, ParserConfigurationException {
        List<PetrolStations> petrolStations = new ArrayList<>();
        File[] files = new File(SFTPDownloader.LOCAL_DIRECTORY).listFiles();
        for (File file : files) {
            petrolStations.add(XMLParser.parsePetrolStationsXML(SFTPDownloader.LOCAL_DIRECTORY + "/" + file.getName()));
        }
        return petrolStations;
    }



}
