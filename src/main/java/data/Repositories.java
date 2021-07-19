package data;

import business.models.Fuel;
import business.models.PetrolStation;
import business.models.PetrolStations;
import properties.PropertiesCache;

import java.sql.*;
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

    public static void writeIntoDataBase(List<PetrolStations> petrolStationsList, int numberOfReports) throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD)
        ){
            createDatabase(conn, DB_NAME);
            conn.setCatalog(DB_NAME.toLowerCase());
            createTables(conn);
            insertData(petrolStationsList, conn, numberOfReports);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try(Statement stmt = conn.createStatement()) {
            createTable(FUELS_TABLE_NAME, SQLHandler.FUELS_TABLE_SQL, stmt, conn);
            createTable(PETROL_STATIONS_TABLE_NAME, SQLHandler.PETROL_STATIONS_TABLE_SQL, stmt, conn);
            createTable(PRICE_LIST_TABLE_NAME, SQLHandler.PRICE_LIST_TABLE_SQL, stmt, conn);
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

    private static void createDatabase(Connection conn, String dbName) throws SQLException {
            if(databaseExists(conn, dbName)) return;

            try(Statement stmt = conn.createStatement()) {
                String sql = "CREATE DATABASE " + dbName;
                stmt.executeUpdate(sql);
            }
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
        try(PreparedStatement preparedStatement = conn.prepareStatement(SQLHandler.PRICE_LIST_INSERT_SQL)){
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
        String sql = "INSERT INTO PETROL_STATIONS (name, address, city) VALUES (?, ?, ?);";
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

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, fuel.getType());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);
            if(rs != null && !rs.isClosed()) rs.close();

            fuelsHashMap.put(fuel.getType(), id);
            return id;
        }
    }
}
