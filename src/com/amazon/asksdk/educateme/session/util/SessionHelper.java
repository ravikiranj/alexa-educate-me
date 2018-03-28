package com.amazon.asksdk.educateme.session.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.amazon.asksdk.educateme.skill.speechlet.EducateMeSpeechlet;

public class SessionHelper {

    private static final Logger log = LoggerFactory.getLogger(SessionHelper.class);

    public static int getReadPointer(String userId, String topicId) {

        Connection connection = getConnection();
        // Make calls to postgres
        if (connection != null) {
            String SQL = "SELECT seq_id FROM educate WHERE user_id = ? AND topic_id = ?";
            int count = 0;

            PreparedStatement pstmt;
            ResultSet rs = null;

            try {
                pstmt = connection.prepareStatement(SQL);
                rs = pstmt.executeQuery(SQL);
                if (rs != null) {
                    return rs.getInt("seq_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        // Return the readPointer
        return -1;
    }

    public static void updateReadPointer(String userId, String topicId, int newReadPointer) {
        // Update the readPointer to next
        Connection connection = getConnection();
        if (connection != null) {
            String SQL = "UPDATE educate " + "SET seq_id = ? "
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
            }
        }
    }

    private static Connection getConnection() {
        try {
            return ConnectionInitializer.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
