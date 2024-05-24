package com.ithcima.utils;

import java.util.*;
import javax.servlet.http.*;

public class readCookies{

public static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){  
    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
    Cookie[] cookies = request.getCookies();
    if(null!=cookies){
        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie);
        }
    }
    return cookieMap;
}
}