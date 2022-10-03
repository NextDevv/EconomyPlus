package sharppvp.plugins.economyplus.db;

import org.bukkit.configuration.Configuration;
import sharppvp.plugins.economyplus.EconomyPlus;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    private static Connection connection;
    static String prefix = "[Economy Plus] (DataBase) ";

    static EconomyPlus plugin = EconomyPlus.getPlugin(EconomyPlus.class);
    static Configuration config = plugin.getConfig();
    public static Connection getConnection() {
        if(connection != null) {
            return connection;
        }
        String host = config.getString("host");
        String name = config.getString("name");
        String username = config.getString("user");
        String password = config.getString("password");

        String url = "jdbc:mysql://"+host+"/"+name+"?characterEncoding=utf8";

        try {
            connection = DriverManager.getConnection(url, username, password);

            System.out.println(prefix+"Connected to the database");


        } catch (SQLException e) {
            System.out.println(prefix+"Failed to connect to the database: " + e.getMessage());
        }

        return connection;
    }

    public static void InitilizeDatabase() {
        try {
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS PlayerEconomy(uuid varchar(36) primary key, Name text, Balance long, Last_login DATE, last_logout DATE);";
            statement.execute(sql);

            System.out.println(prefix+"Successfully created the table in the database");
            statement.close();
        } catch (SQLException e) {
            System.out.println(prefix+"Failed to create the table in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ArrayList<PlayerData> getAllPlayers() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PlayerEconomy");
            ResultSet rs = ps.executeQuery();

            ArrayList<PlayerData> players = new ArrayList<PlayerData>();
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String name = rs.getString("Name");
                long balance = rs.getLong("Balance");
                Date last_login = rs.getDate("Last_login");
                Date last_logout = rs.getDate("last_logout");
                players.add(new PlayerData(uuid, name, balance, last_login, last_logout));
            }

            return players;
        }catch (SQLException e) {
            System.out.println(prefix+"Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static PlayerData getPlayerFromUUID(String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM PlayerEconomy WHERE uuid = ?");
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String name = rs.getString("Name");
                long balance = rs.getLong("Balance");
                java.sql.Date last_login = rs.getDate("Last_login");
                java.sql.Date last_logout = rs.getDate("Last_logout");

                PlayerData playerData = new PlayerData(uuid, name, balance, last_login, last_logout);

                statement.close();

                return playerData;
            }

        } catch (SQLException e) {
            System.out.println(prefix+"Unable to get player data: " + e.getMessage());
            System.out.println(prefix+"At: " + e.getErrorCode());
            e.printStackTrace();
        }

        return null;
    }

    public void createPlayerDatabase(PlayerData d) {
        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO PlayerEconomy(uuid, Name, Balance, Last_login, Last_logout) VALUES(?,?,?,?,?)");
            statement.setString(1, d.getUuid());
            statement.setString(2, d.getName());
            statement.setLong(3, d.getBalance());
            statement.setDate(4, new java.sql.Date(d.getLastLogin().getTime()));
            statement.setDate(5, new java.sql.Date(d.getLastLogout().getTime()));

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println(prefix+"Unable to create player database: " + e.getMessage());
            System.out.println(prefix+"At: " + e.getErrorCode());
            e.printStackTrace();
        }

    }

    public void updatePlayerDatabase(PlayerData d) {
        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("UPDATE PlayerEconomy SET Name = ?, Balance = ?, Last_login = ?, Last_logout = ? WHERE uuid = ?");
            statement.setString(1, d.getName());
            statement.setLong(2, d.getBalance());
            statement.setDate(3, new java.sql.Date(d.getLastLogin().getTime()));
            statement.setDate(4, new java.sql.Date(d.getLastLogout().getTime()));
            statement.setString(5, d.getUuid());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println(prefix+"Unable to create player database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deletePlayerDatabase(PlayerData d) {
        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("DELETE FROM PlayerEconomy WHERE uuid = ?");
            statement.setString(1, d.getUuid());

            statement.executeUpdate();

            statement.close();
            return true;
        }catch (SQLException e) {
            System.out.println(prefix+"Unable to delete player database: " + e.getMessage());
        }
        return false;
    }

    public void closeConnection() {
        try {
            getConnection().close();
        }catch (SQLException e) {
            System.out.println(prefix+"Unable to close connection: " + e.getMessage());
        }
    }
}
