package com.uhrenclan.Https;

//import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class HttpsRequest{
    public Options options;
    public HttpsRequest(Options _options){ options = _options; }
    public HttpsRequest(String _url){
        options = new Options();
        splitUrl(_url);
    }

    public void splitUrl(String _url){
        _url = _url.replaceAll(".*?http.*?://", "");
        Matcher split = Pattern.compile("(.*?(\\..*?)+)($|/|\\?)(.*+)").matcher(_url);
        if (!split.find()) {
            if (_url.substring(0, 1) == "/") options.path = _url;
            else options.host = _url;
        } else {
            options.host = split.group(1);
            options.path = split.group(4).length()>0 && split.group(4).substring(0, 1) == "/" ? split.group(4) : "/" + split.group(4);
        }
    }

    public static String constructURI(Map<String, String> params, String symbol) {
        String uri = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            uri += entry.getKey() + "=" + entry.getValue() +symbol;
        }
        return uri.length()>0?uri.substring(0, uri.length()-1):uri;
    }
    public static String constructURI(Map<String, String> params){ return constructURI(params, "&");}

    public static Map<String, String> parseURI(String url, String symbol){
        Map<String, String> data = new HashMap<String, String>();
        for(String entry : url.split(symbol)){
            try {
                String[] pair = entry.split("=");
                data.put(pair[0].replace(" ", ""), pair[1].replace(" ", ""));
            }catch(Exception e){}
        }
        return data;
    }
    public static Map<String, String> parseURI(String url){ return parseURI(url, "&"); }

    public Response request(boolean followRedirects) {
        AsyncHttpsRequest request = new AsyncHttpsRequest(followRedirects);
        Thread thread = new Thread(request);
        thread.start();
        try { thread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return request.getResponse();
    }

    private class AsyncHttpsRequest implements Runnable{
        private volatile Response presponse;
        private boolean followRedirects;
        private HttpsURLConnection connection;

        public AsyncHttpsRequest(boolean _followRedirects){
            followRedirects = _followRedirects;
        }

        @Override
        public void run(){
        	try {
                connection = (HttpsURLConnection) (new URL(options.protocol+"://"+options.host + options.path)).openConnection();
                connection.setFollowRedirects(false);
                connection.setRequestMethod(options.method);
                
            	for (Map.Entry<String, String> entry : options.header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
                
                ResponseBuilder rBuilder = new ResponseBuilder();
                               
                switch(options.method) {
                case "GET":
                    connection.setRequestProperty("Content-Type", "");
                    connection.setDoInput(true);
                	break;
                case "POST":
                	connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    DataOutputStream ostream = new DataOutputStream(connection.getOutputStream());
                	ostream.write(constructURI(options.params).getBytes());
                	ostream.flush();
                	ostream.close();
                	break;
                }
                
                BufferedReader istream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));//(connection.getResponseCode() > 299) ? connection.getErrorStream() : connection.getInputStream()));
                String data;
                while ((data = istream.readLine()) != null) {
                    rBuilder.append(data);
                }
                
                rBuilder.setStatus(connection.getResponseCode(), connection.getResponseMessage());
                rBuilder.parseHeader(connection.getHeaderFields());

                Response response = rBuilder.constructResponse();
                //System.out.println("\n\n"+ connection.getRequestMethod()+options.method+"   "+response.statusCode+"   "+options.protocol+"://"+options.host + options.path+"\nParams:"+constructURI(options.params)+"\nHeader:"+ options.header.toString()+"\nInHeader:"+ connection.getHeaderFields()+"\n\n");
                
                presponse = response;

                //handling cookies
                if (response.header.containsKey("Set-Cookie") && response.header.get("Set-Cookie").size() > 0) {
                    Map<String, String> setCookie = parseURI(response.header.get("Set-Cookie").get(0), ";");
                    Map<String, String> cookie = parseURI(options.header.get("Cookie"), ";");
                    for(Map.Entry<String, String> entry: setCookie.entrySet()){
                    	if(entry.getKey()!="path") cookie.put(entry.getKey(), entry.getValue());
                    }
                    options.header.put("Cookie", constructURI(cookie, ";"));
                }
                
                if(!followRedirects) return;
                
                if(response.statusCode >= 300 && response.statusCode < 400) {
                	options.path = connection.getHeaderField("Location");
                	options.method = "GET";
                	options.params.clear();
                	presponse = request(true);
                	return;
                }

                Pattern commsyRedirect = Pattern.compile("<meta http-equiv=[\"']refresh[\"'](.|[\\r\\n])*?document.location.href.*?[\"'](.*?)[\"']");
                Matcher matches = commsyRedirect.matcher(response.content);
                if (matches.find()) {
                    options.path = '/' + matches.group(2) + '1';
                    options.method = "GET";
                    options.params.clear();
                    presponse = request(true);
                    return;
                }
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public Response getResponse(){
            return presponse;
        }
    }
}
