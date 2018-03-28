package com.amazon.asksdk.educateme.skill.speechlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.asksdk.educateme.ddb.model.TopicMessage;
import com.amazon.asksdk.educateme.ddb.util.DDBHelper;
import com.amazon.asksdk.educateme.session.util.SessionHelper;
import com.amazon.asksdk.educateme.skill.intent.Intents;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.OutputSpeech;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class EducateMeSpeechlet implements SpeechletV2 {

    private static final Logger log = LoggerFactory.getLogger(EducateMeSpeechlet.class);

    private static final String QUERY = "QUERY";
    private static final String EDUCATE_ME = "Educate Me";
    private static final String WELCOME_TEXT
        = "You can say Alexa ask guru to tell me about bitcoin";
    private static final String UNSUPPORTED_TEXT
        = "This is unsupported. Please try something else.";

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Intent intent = request.getIntent();

        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
            requestEnvelope.getSession().getSessionId());

        String intentName = (intent != null) ? intent.getName() : null;

        log.info("intentName = {}", intentName);

        if (Intents.EDUCATE_INTENT.equals(intentName)) {
            return fetchData(intent, requestEnvelope);
        } else if (Intents.AMAZON_HELP_INTENT.equals(intentName)) {
            return getHelpResponse();
        } else {
            return getAskResponse(EDUCATE_ME, UNSUPPORTED_TEXT);
        }

    }

    private SpeechletResponse fetchData(Intent intent, SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        User user = requestEnvelope.getSession().getUser();
        String userId = user.getUserId();
        String topic = intent.getSlot(QUERY).getValue();
        int readPointer = 0;
        String topicMessage;

        // Get Topic Number from DDB
        String topicId = DDBHelper.getTopicId(topic);

        // Get Pointer Info from SessionManagement
        if (topicId == null) {
            return getAskResponse(EDUCATE_ME, UNSUPPORTED_TEXT);
        } else {
            readPointer = SessionHelper.getReadPointer(userId, topicId);
            log.info(" READ POINTER = " +readPointer);
            if (readPointer < 0) {
                topicMessage = DDBHelper.getTopicData(topic, 0);
                SessionHelper.insertData(userId, topicId, 0);
                log.info(" For readPointer = "+readPointer+ " recieved the following topicMessage ====== " +topicMessage);

            } else {
                topicMessage = DDBHelper.getTopicData(topic, readPointer + 1);

                if (topicMessage == null) {
                    // If we are done reading the topic - remove the topic from Postgres
                    SessionHelper.updateReadPointer(userId, topicId, -1);
                    return getEducateIntentResponse("It was great educating you on "+ topic +". Let me know if I can educate you with anything else");
                }

                log.info(" For readPointer = "+readPointer+ " recieved the following topicMessage ======= " +topicMessage);
                // If we have a topicMessage, update the value in SessionManagement to next readPointer
                SessionHelper.updateReadPointer(userId, topicId, readPointer + 1);
            }

        }
        return getEducateIntentResponse(topicMessage);
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
            requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        return getAskResponse(EDUCATE_ME, WELCOME_TEXT);
    }

    /**
     * Creates a {@code SpeechletResponse} for the get intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getEducateIntentResponse(String topic) {
        log.info("EducateIntent Topic = {}", topic);
        String speechText = topic;

        // Create the Simple card content.
        SimpleCard card = getSimpleCard(EDUCATE_ME, speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        return getAskResponse(EDUCATE_ME, Intents.AMAZON_HELP_INTENT + " -  " + WELCOME_TEXT);
    }

    /**
     * Helper method that creates a card object.
     *
     * @param title title of the card
     * @param content body of the card
     *
     * @return SimpleCard the display card to be sent along with the voice response.
     */
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     *
     * @param speechText the text that should be spoken out to the user.
     *
     * @return an instance of SpeechOutput.
     */
    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     *
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     *
     * @return Reprompt instance.
     */
    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

    /**
     * Helper method for retrieving an Ask response with a simple card and reprompt included.
     *
     * @param cardTitle Title of the card that you want displayed.
     * @param speechText speech text that will be spoken to the user.
     *
     * @return the resulting card and speech text.
     */
    private SpeechletResponse getAskResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        Reprompt reprompt = getReprompt(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
