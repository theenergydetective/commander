/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.restServices;


import com.ted.commander.common.model.VirtualECC;
import com.ted.commander.server.model.CommanderUser;
import com.ted.commander.server.model.alexa.AlexaRequest;
import com.ted.commander.server.model.alexa.AlexaResponse;
import com.ted.commander.server.model.alexa.AlexaResponseAccount;
import com.ted.commander.server.model.alexa.AlexaResponseStatus;
import com.ted.commander.server.repository.CommanderUserRepository;
import com.ted.commander.server.services.AlexaService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Service for creating virtual ECC's and assigning MTU's to them
 */

@RestController
@RequestMapping("/api/alexa")
public class AlexaController {
    final static Logger LOGGER = LoggerFactory.getLogger(AlexaController.class);

    @Autowired
    private CommanderUserRepository commanderUserRepository;

    @Autowired
    AlexaService alexaService;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    AlexaResponse accountLink(@RequestBody AlexaRequest request, HttpServletResponse response) {
        LOGGER.debug("Alexa Post Received: {}", request);
        CommanderUser commanderUser = commanderUserRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

        //Verify the username/password
        AlexaResponse alexaResponse = new AlexaResponse();
        if (commanderUser == null){
            LOGGER.warn("Invalid username/password requested for {}", request.getEmail());
            alexaResponse.setAlexaResponseStatus(AlexaResponseStatus.INVALID_AUTH);
            return alexaResponse;
        }

        if (request.getLocationId() == null || request.getLocationId().equals(0l)){
            alexaResponse.setAlexaResponseStatus(AlexaResponseStatus.LOCATIONS);
            //Check for a list of locations
            List<AlexaResponseAccount> accountList = alexaService.findPossibleLocations(commanderUser.getId());
            if (accountList.size() == 0){
                alexaResponse.setAlexaResponseStatus(AlexaResponseStatus.NO_LOCATIONS);
                return alexaResponse;
            } else if (accountList.size() == 1 && accountList.get(0).getLocationList().size() == 1){
                alexaResponse.setToken(alexaService.generateToken(commanderUser.getId()));
                alexaService.clearLocations(commanderUser.getId());
                alexaService.addLocation(commanderUser.getId(), accountList.get(0).getLocationList().get(0).getLocationId(), "");
                alexaResponse.setAlexaResponseStatus(AlexaResponseStatus.SUCCESS);
                return alexaResponse;
            }
            alexaResponse.setAccountList(accountList);
            return alexaResponse;
        } else {
            alexaResponse.setAlexaResponseStatus(AlexaResponseStatus.SUCCESS);
            alexaResponse.setToken(alexaService.generateToken(commanderUser.getId()));
            alexaService.clearLocations(commanderUser.getId());
            alexaService.addLocation(commanderUser.getId(), request.getLocationId(), "");
            return alexaResponse;
        }
    }
    private String getLinkAccount(){

        return "{" +
                "  \"version\": \"1.0\"," +
                "  \"response\": {" +
                "    \"outputSpeech\": {\"type\":\"PlainText\",\"text\":\"Please go to your Alexa app and link your account to the TED Commander skill.\"}," +
                "    \"card\": {" +
                "      \"type\": \"LinkAccount\"" +
                "    }" +
                "  }" +
                "}";
    }

    private String getStandardResponse(String ssml, boolean endSession){

        return "{ " +
                "        \"version\": \"1.0\", " +
                "        \"sessionAttributes\": {}, " +
                "        \"response\": { " +
                "            \"outputSpeech\": { " +
                "                \"type\": \"SSML\", " +
                "                \"ssml\": \"" + ssml + "\"" +
                "            }, " +
                "            \"shouldEndSession\": " + endSession +
                "         } " +
                "}";
    }

    private String getStandardResponse(String ssml, String cardTitle, String cardText, boolean endSession){

        return "{ " +
                "        \"version\": \"1.0\", " +
                "        \"sessionAttributes\": {}, " +
                "        \"response\": { " +
                "            \"outputSpeech\": { " +
                "                \"type\": \"SSML\", " +
                "                \"ssml\": \"" + ssml + "\"" +
                "            }, " +
                "            \"card\": { " +
                "                \"type\": \"Simple\", " +
                "                \"title\": \"" + cardTitle + "\", " +
                "                \"content\": \"" + cardText + "\"" +
                "            }, " +
                "            \"shouldEndSession\": " + endSession +
                "         } " +
                "}";
    }


    private String getStandardResponse(String ssml){
        return getStandardResponse(ssml, true);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.POST)
    public String getSummary(@RequestBody String data){

        JSONObject root = new JSONObject(data);

        LOGGER.debug(root.toString(5));

        JSONObject request = root.getJSONObject("request");
        String type = request.getString("type");

        //Verify Account
        CommanderUser commanderUser;
        try {
            JSONObject session = root.getJSONObject("session");
            JSONObject user = session.getJSONObject("user");

            if (!user.has("accessToken")){
                return getLinkAccount();
            }
            String accessToken = user.getString("accessToken");
            commanderUser = commanderUserRepository.findByAlexaAccessToken(accessToken);



            if (commanderUser == null) {
                LOGGER.warn("count not find token {}", accessToken);
                return getLinkAccount();
            }
        } catch (Exception ex){
            LOGGER.warn("Could not find user: {}", ex);
            return getLinkAccount();
        }


        if (type.equals("LaunchRequest")){
            return getStandardResponse(alexaService.getOpen(), false);
        }


        //Process Request
        try {


            if (request != null && request.has("intent")) {
                JSONObject intent = request.getJSONObject("intent");

                VirtualECC virtualECC = alexaService.getAlexaLocationForUser(commanderUser.getId());



                if (virtualECC == null) {
                    LOGGER.warn("Invalid location request: {}", request.toString(5));
                    return getStandardResponse("<speak>I'm sorry. You do not have any locations linked. Please try again later</speak>");
                }

                switch (intent.getString("name")) {
                    case "AMAZON.HelpIntent":{
                        return getStandardResponse(alexaService.getHelp(),
                                "Example Questions",
                                alexaService.getHelpQuestions(), false);
                    }
                    case "AMAZON.StopIntent":{
                        return getStandardResponse("<speak>Good-bye</speak>");
                    }
                    case "TEDSummary": {
                        return getStandardResponse(alexaService.getSummary(virtualECC));
                    }
                    case "TEDUsageNow": {
                        return getStandardResponse(alexaService.getUsageNow(virtualECC));
                    }
                    case "TEDUsage": {
                        JSONObject slots = intent.getJSONObject("slots");
                        if (slots.has("history")){
                            JSONObject history = slots.getJSONObject("history");
                            if (history.has("value")){
                                 boolean today = (history.getString("value").toUpperCase().equals("TODAY"));
                                return getStandardResponse(alexaService.getUsage(virtualECC, today));
                            }
                        }
                        return getStandardResponse(alexaService.getUsageNow(virtualECC));
                    }
                    case "TEDBill": {
                        return getStandardResponse(alexaService.getProjected(virtualECC));
                    }
                    case "TEDSpent": {
                        JSONObject slots = intent.getJSONObject("slots");
                        if (slots.has("history")){
                            JSONObject history = slots.getJSONObject("history");
                            if (history.has("value")){
                                boolean today = (history.getString("value").toUpperCase().equals("TODAY"));
                                return getStandardResponse(alexaService.getSpent(virtualECC, today));
                            }
                        }
                        return getStandardResponse(alexaService.getUsageNow(virtualECC));
                    }
                    case "TEDBillingCycle": {
                        return getStandardResponse(alexaService.getDaysLeft(virtualECC));
                    }
                    case "TEDLoads": {
                        return getStandardResponse(alexaService.activeLoads(virtualECC));
                    }
                    case "TEDGenNow": {
                        return getStandardResponse(alexaService.generationNow(virtualECC));
                    }
                    case "TEDGen": {
                        JSONObject slots = intent.getJSONObject("slots");
                        if (slots.has("history")){
                            JSONObject history = slots.getJSONObject("history");
                            if (history.has("value")){
                                boolean today = (history.getString("value").toUpperCase().equals("TODAY"));
                                return getStandardResponse(alexaService.getGeneration(virtualECC, today));
                            }
                        }
                        return getStandardResponse(alexaService.generationNow(virtualECC));
                    }
                }
            }
        } catch (Exception ex){
            LOGGER.error("Alexa Exeption", ex);
        }
        return getStandardResponse("<speak>I do not understand what you are asking for. Please try again</speak>");

    }

}
