package com.amazon.asksdk.educateme.session.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class SessionHelper {

    private static final Logger log = LoggerFactory.getLogger(SessionHelper.class);

    public static int getReadPointer(String userId, String topicId) {

        Connection connection = getConnection();

        if (connection != null) {
            PreparedStatement pstmt;
            ResultSet rs = null;

            String SQL = "SELECT seq_id FROM educate_user_topic_state WHERE user_id = ? AND topic_id = ?";

            try {
                pstmt = connection.prepareStatement(SQL);
                pstmt.setString(1, userId);
                pstmt.setString(2, topicId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    int rdPtr = rs.getInt("seq_id");
                    log.info(" ReadPointer fetched from db = " + rdPtr);
                    return rdPtr;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return -1;
    }

    public static void updateReadPointer(String userId, String topicId, int newReadPointer) {
        // Update the readPointer to next
        Connection connection = getConnection();
        if (connection != null) {
            String SQL = "UPDATE educate_user_topic_state " + "SET seq_id = ? "
                + "WHERE user_id = ? AND topic_id = ? ";

            int affectedrows = 0;
            PreparedStatement pstmt;

            try {
                pstmt = connection.prepareStatement(SQL);
                pstmt.setInt(1, newReadPointer);
                pstmt.setString(2, userId);
                pstmt.setString(3, topicId);
                affectedrows = pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (affectedrows <= 0) {
                log.info("No rows updated");
            } else {
                log.info("Updated the value of readPointer in db " + newReadPointer);
            }
        }
    }

    private static Connection getConnection() {
        try {
            log.info("Connection Initialized");
            return ConnectionInitializer.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info("Returning null connection");
        return null;
    }

    /*
    Remove this code
     */
    public static void insertData(String userId, String topicId, int newReadPointer) {
        Connection connection = getConnection();
        if (connection != null) {
            String sql = "insert into educate_user_topic_state(user_id, topic_id, seq_id) values('" + userId + "', '"
                + topicId + "'," + newReadPointer + ") ON CONFLICT(user_id, topic_id) DO UPDATE SET seq_id = " + newReadPointer;
            try {
                Statement statement = connection.createStatement();
                boolean status = statement.execute(sql);
                log.info("TRUE ? FALSE ===== " +status);
            } catch (SQLException e) {
                log.info("ex "+e);
                e.printStackTrace();
            }

            log.info("SQL inserted - " + sql);

        }
    }

}
