package com.amazon.asksdk.educateme.ddb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;

import com.amazon.asksdk.educateme.ddb.model.Topic;
import com.amazon.asksdk.educateme.ddb.model.TopicMessage;
import com.amazon.asksdk.educateme.session.util.SessionHelper;

public class DDBHelper {

    private static final Logger log = LoggerFactory.getLogger(DDBHelper.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String getTopicId(String topicName) {

        String url = " http://ec2-35-164-43-144.us-west-2.compute.amazonaws.com/getTopicId?topic=" + topicName;

        URL obj = null;
        HttpURLConnection con = null;
        String response = null;
        try {
            con = createGetRequest(con, url);
            log.info("Response code = " + con.getResponseCode());
            log.info("Response message = " + con.getResponseMessage());

            response = getResponseData(con).get("id").toString();
            log.info("TopicId Response from DDB = " + response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String getTopicData(String topicName, int readPointer) {
        String url = "http://ec2-35-164-43-144.us-west-2.compute.amazonaws.com/getTopic?topic=" + topicName + "&index="
            + readPointer;


        URL obj = null;
        HttpURLConnection con = null;
        Map responseTopicMessage = null;
        try {

            con = createGetRequest(con, url);
            log.info("Response code = " + con.getResponseCode());
            log.info("Response message = " + con.getResponseMessage());

            if (con.getResponseCode()!=200) {
                return null;
            }
            responseTopicMessage = getResponseData(con);

            log.info("TopicData Response from DDB = " + responseTopicMessage.get("fact").toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseTopicMessage.get("fact").toString();
    }

    private static HttpURLConnection createGetRequest(HttpURLConnection con, String url)
        throws IOException {

        URL urlObject = new URL(url);
        con = (HttpURLConnection) urlObject.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
    }

    private static String getResponse(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        log.info("-----" +response.toString());
        return response.toString();
    }

    private static Map getResponseData(HttpURLConnection con) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(con.getInputStream(), Map.class);
        log.info("MAP   =---> "+jsonMap.toString());
        //TopicMessage topicMessage = mapper.readValue(getResponse(con), TopicMessage.class);
        //return topicMessage;
        return jsonMap;
    }
}
