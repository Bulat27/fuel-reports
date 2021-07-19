package data;

public final class SQLHandler {

    public static final String FUELS_TABLE_SQL;
    public static final String PETROL_STATIONS_TABLE_SQL;
    public static final String PRICE_LIST_TABLE_SQL;
    public static final String PRICE_LIST_INSERT_SQL;

    private SQLHandler(){}

    static{
        FUELS_TABLE_SQL = "CREATE TABLE FUELS(\n" +
                "\tid INT AUTO_INCREMENT,\n" +
                "    name VARCHAR(40) NOT NULL UNIQUE,\n" +
                "    PRIMARY KEY (id)\n" +
                ");";

       PETROL_STATIONS_TABLE_SQL = "CREATE TABLE PETROL_STATIONS(\n" +
               "\tid INT AUTO_INCREMENT,\n" +
               "    name VARCHAR(40) NOT NULL,\n" +
               "    address VARCHAR(40) NOT NULL,\n" +
               "    city VARCHAR(40) NOT NULL,\n" +
               "    PRIMARY KEY (id)\n" +
               ");";

       PRICE_LIST_TABLE_SQL = "CREATE TABLE PRICE_LIST(\n" +
               "\tid INT,\n" +
               "    petrol_station_id INT NOT NULL,\n" +
               "    fuel_id INT NOT NULL,\n" +
               "    price DECIMAL(7,2) NOT NULL,\n" +
               "    date DATE NOT NULL,\n" +
               "    PRIMARY KEY (id),\n" +
               "    FOREIGN KEY (petrol_station_id) REFERENCES PETROL_STATIONS (id),\n" +
               "    FOREIGN KEY (fuel_id) REFERENCES FUELS (id)\n" +
               ");";

       PRICE_LIST_INSERT_SQL = "INSERT INTO PRICE_LIST (id, petrol_station_id, fuel_id, price, date)" +
               " VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
               "id = id, petrol_station_id = petrol_station_id, fuel_id = fuel_id, price = price, date = date;";
    }
}
