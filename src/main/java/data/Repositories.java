package data;

import business.models.Fuel;
import business.models.PetrolStation;
import business.models.PetrolStations;
import business.services.SFTPDownloader;
import business.services.XMLParser;
import org.xml.sax.SAXException;
import properties.PropertiesCache;
import properties.SQLHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Repositories {

    private static final String DB_URL;
    private static final String USER_NAME;
    private static final String PASSWORD;
    private static final String DB_NAME;
    private static final String FUELS_TABLE_NAME;
    private static final String PETROL_STATIONS_TABLE_NAME;
    private static final String PRICE_LIST_TABLE_NAME;

    static{
        PropertiesCache properties = PropertiesCache.getInstance();
        DB_URL = properties.getProperty("dbUrl");
        USER_NAME = properties.getProperty("dbUserName");
        PASSWORD = properties.getProperty("dbPassword");
        DB_NAME = "PETROL";
        FUELS_TABLE_NAME = "FUELS";
        PETROL_STATIONS_TABLE_NAME = "PETROL_STATIONS";
        PRICE_LIST_TABLE_NAME = "PRICE_LIST";
    }

    private Repositories(){}

    private static void writeIntoDataBase(List<PetrolStations> petrolStationsList, int numberOfReports) throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            Statement stmt = conn.createStatement()
        ){
            createDatabase(conn, stmt, DB_NAME);
            conn.setCatalog(DB_NAME.toLowerCase());
            createTables(conn);
            insertData(petrolStationsList, conn, numberOfReports);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try(Statement stmt = conn.createStatement()) {
            String fuelSQL = SQLHandler.FUELS_TABLE_SQL;
            String psSQL = SQLHandler.PETROL_STATIONS_TABLE_SQL;
            String plSQL = SQLHandler.PRICE_LIST_TABLE_SQL;

            createTable(FUELS_TABLE_NAME, fuelSQL, stmt, conn);
            createTable(PETROL_STATIONS_TABLE_NAME, psSQL, stmt, conn);
            createTable(PRICE_LIST_TABLE_NAME, plSQL, stmt, conn);
        }
    }

    private static void createTable(String tableName, String sql, Statement stmt, Connection conn) throws SQLException {
        if(tableExists(conn, tableName)) return;

        stmt.executeUpdate(sql);
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData dm = conn.getMetaData();
        try(ResultSet rs = dm.getTables(DB_NAME.toLowerCase(), null, tableName, new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    private static void createDatabase(Connection conn, Statement stmt, String dbName) throws SQLException {
            if(databaseExists(conn,dbName)) return;

            String sql = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(sql);
    }

    private static boolean databaseExists(Connection conn, String dbName) throws SQLException {
        try(ResultSet rs = conn.getMetaData().getCatalogs()){
            while(rs.next()){
                String s = rs.getString(1);
                if(s.equals(dbName.toLowerCase())) return true;
            }
        }
        return false;
    }

    private static void insertData(List<PetrolStations> petrolStationsList, Connection conn, int numberOfReports) throws SQLException {
        int fuelId;
        int psId;
        HashMap<String, Integer> fuelsHashMap = getHashMap(conn, FUELS_TABLE_NAME);
        HashMap<String, Integer> petrolStationHashMap = getHashMap(conn, PETROL_STATIONS_TABLE_NAME);

        int count = 1;
        for (PetrolStations p : petrolStationsList) {
            for (PetrolStation petrolStation: p.getPetrolStationList()) {
                for (Fuel fuel : petrolStation.getFuels()){
                     fuelId = insertIntoFuels(fuel, fuelsHashMap , conn);
                     psId = insertIntoPetrolStations(petrolStation, petrolStationHashMap, conn);
                     insertIntoPriceList(petrolStation, psId, fuel, fuelId, conn, count);
                     count++;
                     if(count == numberOfReports + 1) return;
                }
            }
        }
    }

    private static void insertIntoPriceList(PetrolStation petrolStation, int psId, Fuel fuel, int fuelId, Connection conn, int id) throws SQLException {
        String sql = "INSERT INTO PRICE_LIST (id, petrol_station_id, fuel_id, price, date)" +
                " VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "id = id, petrol_station_id = petrol_station_id, fuel_id = fuel_id, price = price, date = date;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, psId);
            preparedStatement.setInt(3, fuelId);
            preparedStatement.setDouble(4, fuel.getPrice());
            preparedStatement.setDate(5, Date.valueOf(petrolStation.getDate()));
            preparedStatement.executeUpdate();
        }
    }

    private static int insertIntoPetrolStations(PetrolStation petrolStation, HashMap<String, Integer> petrolStationHashMap, Connection conn) throws SQLException {
        if(petrolStationHashMap.containsKey(petrolStation.getName())) return petrolStationHashMap.get(petrolStation.getName());
        String sql = "INSERT INTO PETROL_STATIONS (name, adress, city) VALUES (?, ?, ?);";
        int id = 0;

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, petrolStation.getName());
            preparedStatement.setString(2, petrolStation.getAddress());
            preparedStatement.setString(3, petrolStation.getCity());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) id = rs.getInt(1);
            if(rs != null && !rs.isClosed()) rs.close();

            petrolStationHashMap.put(petrolStation.getName(), id);
            return id;
        }
    }

    private static HashMap<String, Integer> getHashMap(Connection conn, String tableName) throws SQLException {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String sql = "SELECT * FROM " + tableName + ";";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                hashMap.put(rs.getString(2), Integer.valueOf(rs.getString(1)));
            }
            if(rs != null && !rs.isClosed()) rs.close();
        }
        return hashMap;
    }

    private static int insertIntoFuels(Fuel fuel, HashMap<String, Integer> fuelsHashMap, Connection conn) throws SQLException {
        if(fuelsHashMap.containsKey(fuel.getType())) return fuelsHashMap.get(fuel.getType());
        String sql = "INSERT INTO FUELS (name) VALUES (?);";
        int id = 0;

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            preparedStatement.setString(1, fuel.getType());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);
            if(rs != null && !rs.isClosed()) rs.close();

            fuelsHashMap.put(fuel.getType(), id);
            return id;
        }
    }

    public static void main(String[] args) {
        try {
            //SFTPDownloader.downloadFiles();
            writeIntoDataBase(returnPetrolStations(), 5);
        } catch (SQLException | IOException | SAXException | ParserConfigurationException e) {
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
