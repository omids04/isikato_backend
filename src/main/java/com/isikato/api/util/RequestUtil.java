package com.isikato.api.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

}
