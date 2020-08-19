package com.uhrenclan.Https;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Options {
    public String host, path;
    public String method = "GET", protocol = "https";
    public Map<String, String> header = new HashMap<String, String>(){{
        put("Cookie", "");
        put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
    }};
    public Map<String, String> params = new HashMap<String, String>();

    public Options(String _host, String _path){
        host = _host;
        path = _path;
    }
    public Options(String _host, String _path, Map<String, String> _cookie){
        host = _host;
        path = _path;
        header.put("Cookie", HttpsRequest.constructURI(_cookie, ";"));
    }
    public Options(){
        host = path = "";
    }

    public Map<String, String> getCookies(){
        if(header.get("Cookie")==null) return new HashMap<String, String>();
        return HttpsRequest.parseURI(header.get("Cookie"), ";");
    }
}
