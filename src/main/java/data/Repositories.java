package data;

import business.models.Fuel;
import business.models.PetrolStation;
import business.models.PetrolStations;
import business.services.SFTPDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.PropertiesCache;

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
    private static final String CONFIG_TABLE_NAME;
    private static final String DISTINCTION_COLUMN_FUELS;
    private static final Logger LOGGER;

    static{
        PropertiesCache properties = PropertiesCache.getInstance();
        DB_URL = properties.getProperty("dbUrl");
        USER_NAME = properties.getProperty("dbUserName");
        PASSWORD = properties.getProperty("dbPassword");
        DB_NAME = "PETROL";
        FUELS_TABLE_NAME = "FUELS";
        PETROL_STATIONS_TABLE_NAME = "PETROL_STATIONS";
        PRICE_LIST_TABLE_NAME = "PRICE_LIST";
        CONFIG_TABLE_NAME = "CONFIG";
        DISTINCTION_COLUMN_FUELS = "name";
        LOGGER = LoggerFactory.getLogger(Repositories.class);
    }

    private Repositories(){}

    public static void writeIntoDataBase(List<PetrolStations> petrolStationsList, int numberOfReports) throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD)
        ){
            createDatabase(conn);
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

    private static void createDatabase(Connection conn) throws SQLException {
            if(databaseExists(conn)) return;

            try(Statement stmt = conn.createStatement()) {
                String sql = "CREATE DATABASE " + DB_NAME;
                stmt.executeUpdate(sql);
            }
    }

    private static boolean databaseExists(Connection conn) throws SQLException {
        try(ResultSet rs = conn.getMetaData().getCatalogs()){
            while(rs.next()){
                String s = rs.getString(1);
                if(s.equals(DB_NAME.toLowerCase())) return true;
            }
        }
        return false;
    }

    private static void insertData(List<PetrolStations> petrolStationsList, Connection conn, int numberOfReports) throws SQLException {
        int fuelId;
        int psId;
        HashMap<String, Integer> fuelsHashMap = getMapOfIdsByName(conn);
        HashMap<String, Integer> petrolStationHashMap = getMapOfIdsByPSKey(conn);

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
        String petrolStationKey = getPetrolStationKey(petrolStation);
        if(petrolStationHashMap.containsKey(petrolStationKey)) return petrolStationHashMap.get(petrolStationKey);
        String sql = "INSERT INTO PETROL_STATIONS (name, address, city) VALUES (?, ?, ?);";
        int id = 0;

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, petrolStation.getName());
            preparedStatement.setString(2, petrolStation.getAddress());
            preparedStatement.setString(3, petrolStation.getCity());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) id = rs.getInt(1);

            petrolStationHashMap.put(petrolStationKey, id);
            return id;
        }
    }

    private static String getPetrolStationKey(PetrolStation pS){
        return (pS.getName() + pS.getAddress() + pS.getCity()).toLowerCase();
    }

    private static String getPetrolStationKey(ResultSet rs) throws SQLException {
        return (rs.getString("name") + rs.getString("address") + rs.getString("city")).toLowerCase();
    }

    private static HashMap<String, Integer> getMapOfIdsByName(Connection conn) throws SQLException {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String sql = "SELECT * FROM " + FUELS_TABLE_NAME + ";";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                hashMap.put(rs.getString(DISTINCTION_COLUMN_FUELS).toLowerCase(), Integer.valueOf(rs.getString(1)));
            }
        }
        return hashMap;
    }

    private static HashMap<String, Integer> getMapOfIdsByPSKey(Connection conn) throws SQLException {
        HashMap<String, Integer> hashMap = new HashMap<>();
        String sql = "SELECT * FROM " + PETROL_STATIONS_TABLE_NAME + ";";

        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                hashMap.put(getPetrolStationKey(rs), Integer.valueOf(rs.getString(1)));
            }
        }
        return hashMap;
    }

    private static int insertIntoFuels(Fuel fuel, HashMap<String, Integer> fuelsHashMap, Connection conn) throws SQLException {
        if(fuelsHashMap.containsKey(fuel.getType().toLowerCase())) return fuelsHashMap.get(fuel.getType().toLowerCase());
        String sql = "INSERT INTO FUELS (name) VALUES (?);";
        int id = 0;

        try(PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, fuel.getType());
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) id = rs.getInt(1);

            fuelsHashMap.put(fuel.getType().toLowerCase(), id);
            return id;
        }
    }

    public static String getDefaultDestination() throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD)){
            createDatabase(conn);

            conn.setCatalog(DB_NAME.toLowerCase());
            try(Statement stmt = conn.createStatement()) {
                createTable(CONFIG_TABLE_NAME, SQLHandler.CONFIG_TABLE_SQL, stmt, conn);
                return getPath(conn);
            }
        }
    }

    private static String getPath(Connection conn) throws SQLException {
        String sql = "SELECT directory_path FROM CONFIG WHERE id = 1;";
        try(Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) return rs.getString(1);
        }
        return SFTPDownloader.LOCAL_DIRECTORY;
    }

    public static void updateDefaultDestination(String directoryPath) throws SQLException {
        String sql = "INSERT INTO CONFIG (id, directory_path) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = id, directory_path = '" + directoryPath + "';";
        try(Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, USER_NAME, PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, directoryPath);
            preparedStatement.executeUpdate();
        }
    }

    public static List<Fuel> getTheReport(String period, String fuelType, String ps, String city) throws SQLException {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD)){
           if(!dataReady(conn)) return null;

            List<String> addedParameters = new ArrayList<>();
            String[] strArr = period.split("-");
            String sql = generateReportQuery(strArr, fuelType, ps, city, addedParameters);
            int count = 1;

            try(PreparedStatement preparedStatement = conn.prepareStatement(sql)){
                preparedStatement.setString(count, strArr[0]);
                count++;

                if(strArr.length >= 2) count = processTheParameter("month", strArr[1], preparedStatement, count, addedParameters);
                if(strArr.length >= 3) count = processTheParameter("day", strArr[2], preparedStatement, count, addedParameters);
                count = processTheParameter("fuelType", fuelType, preparedStatement, count, addedParameters);
                count = processTheParameter("ps", ps, preparedStatement, count, addedParameters);
                processTheParameter("city", city, preparedStatement, count, addedParameters);

                ResultSet rs = preparedStatement.executeQuery();
                return getReportList(rs);
            }
        }
    }

    private static List<Fuel> getReportList(ResultSet rs) throws SQLException {
        List<Fuel> reportList = new ArrayList<>();
        while(rs.next()){
            reportList.add(new Fuel(rs.getString(1), rs.getDouble(2)));
        }
        return reportList;
    }

    private static int processTheParameter(String parameter, String value, PreparedStatement prstmt, int count, List<String> addedParameters) throws SQLException {
        if(addedParameters.contains(parameter)) {
            prstmt.setString(count, value);
            count++;
        }
        return count;
    }

    private static boolean dataReady(Connection conn) throws SQLException {
        if(!databaseExists(conn)){
            LOGGER.info("The database doesn't exist! Please download the data!");
            return false;
        }
        conn.setCatalog(DB_NAME.toLowerCase());
        if(!tableExists(conn, PETROL_STATIONS_TABLE_NAME)){
            LOGGER.info("The database doesn't exist! Please download the data!");
            return false;
        }
        return true;
    }

    private static String generateReportQuery(String[] strArr, String fuelType, String ps, String city, List<String> addedParameters) {
        StringBuilder sql = new StringBuilder(SQLHandler.REPORT_SQL);

        if(strArr.length >= 2){
            sql.append("AND EXTRACT(MONTH FROM pl.date) = ? ");
            addedParameters.add("month");
        }

        if(strArr.length >= 3){
            sql.append("AND EXTRACT(DAY FROM pl.date) = ? ");
            addedParameters.add("day");
        }

        if(fuelType != null){
            sql.append("AND f.name = ? ");
            addedParameters.add("fuelType");
        }

        if(ps != null){
            sql.append("AND ps.name = ? ");
            addedParameters.add("ps");
        }

        if(city != null){
            sql.append("AND ps.city = ? ");
            addedParameters.add("city");
        }
        sql.append("GROUP BY fuel_id;");
        return sql.toString();
    }
}
