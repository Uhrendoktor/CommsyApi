package com.uhrenclan.Https;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {
    public final int statusCode;
    public final String statusMessage;
    public final String content;
    public final Map<String, List<String>> header;

    public final boolean error;

    public Response(int _statusCode, String _statusMessage, String _content, Map<String, List<String>> _header, boolean _error){
        statusCode = _statusCode;
        statusMessage = _statusMessage;
        content = _content;
        header = _header;
        error = _error;
    }

    public Map<String, String> getCookies(){
        if(header.get("Cookie")==null) return new HashMap<String, String>();
        return HttpsRequest.parseURI(header.get("Cookie").get(0), "; ");
    }
}
