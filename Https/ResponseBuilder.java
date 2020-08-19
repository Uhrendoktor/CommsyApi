package com.uhrenclan.Https;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ResponseBuilder {

    String content = "";
    public void append(String data){
        content += data;
    }

    private Map<String, List<String>> header = new HashMap<String, List<String>>();
    public void parseHeader(Map<String, List<String>> _header){
        for(Map.Entry<String, List<String>> entry : _header.entrySet()){
            if(entry.getKey() != null) header.put(entry.getKey(), entry.getValue());
        }
    }

    private int statusCode;
    private String statusMessage;
    public void setStatus (int _code, String _message){
        statusCode = _code;
        statusMessage = _message;
    }

    public Response constructResponse(){
        return new Response(statusCode, statusMessage, content, header, statusCode>299);
    }
}
