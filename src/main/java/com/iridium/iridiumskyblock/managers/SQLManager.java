package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.SQL;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLManager {
    private HikariDataSource hikariDataSource = new HikariDataSource();

    public SQLManager() {
        setupConnection();
    }

    private void setupConnection() {
        final SQL sql = IridiumSkyblock.getSql();
        hikariDataSource.setMaximumPoolSize(10);
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
            hikariDataSource.setJdbcUrl("jdbc:sqlite:" + dataFolder);
//            try {
//                Class.forName("org.sqlite.JDBC");
//                return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
//            } catch (SQLException ex) {
//                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQL exception on initialize", ex);
//            } catch (ClassNotFoundException ex) {
//                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "Could not find SQL library");
//            }
        } else {
            //Use SQL
            hikariDataSource.setUsername(sql.username);
            hikariDataSource.setPassword(sql.password);
            hikariDataSource.setJdbcUrl("jdbc:mysql://" + sql.host + ":" + sql.port + "/" + sql.database);
//            try {
//                Class.forName("com.mysql.jdbc.Driver");
//                return DriverManager.getConnection("jdbc:mysql://" + sql.host + ":" + sql.port + "/" + sql.database, sql.username, sql.password);
//            } catch (SQLException ex) {
//                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
//            } catch (ClassNotFoundException ex) {
//                IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "Could not find SQL library");
//            }
        }
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

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islanddata "
                    + "(islandID INTEGER, value DOUBLE, votes INTEGER, private BOOLEAN);");

            connection.close();

        } catch (SQLException ex) {
            IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on Creating Tables", ex);
        }
    }

    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
