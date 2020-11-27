package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.SQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLManager {
    private Connection setupConnection() {
        final SQL sql = IridiumSkyblock.getSql();
        //Check if we need to use SQL or SQLLite
        if (sql.username.isEmpty()) {
            //If the username is empty, continue with sql lite
            File dataFolder = new File(IridiumSkyblock.getInstance().getDataFolder(), sql.database + ".db");
            if (!dataFolder.exists()) {
                //Create the .db file if it doesnt exist
                try {
                    dataFolder.createNewFile();
                } catch (IOException e) {
                    IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "File write error: " + sql.database + ".db");
                }
            }
            try {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            } catch (SQLException ex) {
                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQL exception on initialize", ex);
            } catch (ClassNotFoundException ex) {
                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "Could not find SQL library");
            }
        } else {
            //Use SQL
            try {
                Class.forName("com.mysql.jdbc.Driver");
                return DriverManager.getConnection("jdbc:mysql://" + sql.host + ":" + sql.port + "/" + sql.database, sql.username, sql.password);
            } catch (SQLException ex) {
                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
            } catch (ClassNotFoundException ex) {
                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "Could not find SQL library");
            }
        }
        return null;
    }

    public void createTables() {
        try {
            Connection connection = getConnection();
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS users "
                    + "(UUID VARCHAR(255), json TEXT, PRIMARY KEY (UUID));");


            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS claims "
                    + "(x INTEGER, z INTEGER, island INTEGER);");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islands "
                    + "(id INTEGER, json TEXT, PRIMARY KEY (id));");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islandmanager "
                    + "(nextID INTEGER, length INTEGER, current INTEGER, x DOUBLE, y DOUBLE, direction VARCHAR(10));");

            connection.close();

        } catch (SQLException ex) {
            IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on Creating Tables", ex);
        }
    }

    public Connection getConnection() {
        return setupConnection();
    }
}
