package com.uhrenclan.commsyapi;

//import android.util.Log;

import android.util.Log;

import com.uhrenclan.Https.HttpsRequest;
import com.uhrenclan.Https.Response;
import com.uhrenclan.commsyapi.room.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommsyClient {
    public String SID, PHPSESSID, UID, SECURITY_TOKEN, AUTH_SOURCE;
    public String root_url = "unterricht.sh.schulcommsy.de";
    
    private boolean authenticated = false;
    private HttpsRequest request;
    
    public Map<String, Room> rooms;

    public CommsyClient(String _SID, String _PHPSESSID, String _UID){
        SID = _SID;
        PHPSESSID = _PHPSESSID;
        UID = _UID;
        authenticated = true;
        setupRooms();
    }
    public CommsyClient(){}

    public void login(final String username, final String password) throws CommsyInvalidLoginDataException, CommsyServerErrorException {
        if(authenticated) return;

        HttpsRequest request = new HttpsRequest(root_url);
        Response response;

        //First SID
        response = request.request(true);       
        SID = request.options.getCookies().get("SID");

        //parsing of form
        Matcher form = Pattern.compile("\\<form.*?action=[\"'](.*?)[\"'] name=[\"']login[\"']\\>((.|[\\r\\n])*?)\\<\\/form\\>").matcher(response.content);
        if(!form.find()) return;
        final Map<String, String> inputs = new HashMap<String, String>();
        Matcher input = Pattern.compile("\\<input[^<]*?name=[\"']([^<]*?)[\"'][^<]*?value=[\"']([^<]*?)[\"'][^<]*?\\>").matcher(form.group(0));
        while(input.find()) {
            inputs.put(input.group(1), input.group(2));
        }
        SECURITY_TOKEN = inputs.get("security_token");
        AUTH_SOURCE = inputs.get("auth_source");

        //preparing next request (login post)
        request.options.path = "/"+form.group(1).replaceAll("amp;", "");
        request.options.method = "POST";
        request.options.params = new HashMap<String, String>(){{
            put("security_token", SECURITY_TOKEN);
            put("auth_source", AUTH_SOURCE);
            put("user_id", username);
            put("password", password);
            put("option", inputs.get("option"));
        }};
        response = request.request(true);

        Matcher login = Pattern.compile("dashboard").matcher(request.options.path);
        if(!login.find()) throw new CommsyInvalidLoginDataException("Invalid Login Data");
        Matcher error = Pattern.compile("<title>.*?(Error|error).*?<\\/title>").matcher(response.content);
        if(error.find()) throw new CommsyServerErrorException(error.group(1));

        Map<String, String> cookies = request.options.getCookies();
        //Second SID
        SID = cookies.get("SID");
        PHPSESSID = cookies.get("PHPSESSID");
        Matcher uid = Pattern.compile("([\\d]+)").matcher(request.options.path);
        uid.find();
        UID = uid.group(1);
        
        System.out.println("\nSID:"+SID+" \nPHPSESSID:"+PHPSESSID+" \nUID:"+UID+"\n");
        
        authenticated = true;
        
        setupRooms();
    }
    
    public Response request(HttpsRequest _request) {
        Map<String, String> cookie = HttpsRequest.parseURI(_request.options.header.get("Cookie"), ";");
        cookie.put("SID", SID);
        cookie.put("PHPSESSID", PHPSESSID);
        _request.options.header.put("Cookie", HttpsRequest.constructURI(cookie, ";"));
        _request.options.host = root_url;
        
        return _request.request(true);
    }
    
    public void setupRooms() {
    	if(!authenticated) return;
    	rooms = new HashMap<String, Room>();
    	rooms.put("Dashboard", new DashboardRoom(this));
    }

    public boolean isAuthenticated(){ return authenticated; }

    public String toString(){
        return String.format("SID:%s\nPHPSESSID:%s\nUID:%s\nAUTH:%b\n", SID, PHPSESSID, UID, authenticated);
    }

    public class CommsyInvalidLoginDataException extends Exception{
        public CommsyInvalidLoginDataException(String msg){
            super(msg);
        }
    }

    public class CommsyServerErrorException extends Exception{
        public CommsyServerErrorException(String msg){
            super(msg);
        }
    }
}
