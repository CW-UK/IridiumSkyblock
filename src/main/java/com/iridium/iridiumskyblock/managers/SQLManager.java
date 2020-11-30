package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.SQL;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLManager {
    private final HikariDataSource hikariDataSource = new HikariDataSource();

    public SQLManager() {
        setupConnection();
    }

    private void setupConnection() {
        final SQL sql = IridiumSkyblock.getSql();
        hikariDataSource.setMaximumPoolSize(sql.poolSize);
        hikariDataSource.setLeakDetectionThreshold(3000);
        if (sql.username.isEmpty()) {
            //SQL Lite
            hikariDataSource.setJdbcUrl("jdbc:sqlite:" + new File(IridiumSkyblock.getInstance().getDataFolder(), sql.database + ".db"));
        } else {
            //Use SQL
            hikariDataSource.setUsername(sql.username);
            hikariDataSource.setPassword(sql.password);
            hikariDataSource.setJdbcUrl("jdbc:mysql://" + sql.host + ":" + sql.port + "/" + sql.database);
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
