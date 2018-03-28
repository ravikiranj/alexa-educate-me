package com.amazon.asksdk.educateme.session.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionInitializer {

    private static String url = "jdbc:postgresql://alexa-educate-me.chgnmfendvhp.us-west-2.rds.amazonaws.com/educate";
    private static String user = "alexa";
    private static String password = "alexa2018";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
