package com.reedelk.rest.commons;

import io.netty.handler.codec.http.HttpResponseStatus;

public class IsSuccessful {

    public static boolean status(HttpResponseStatus status) {
        int statusCode = status.code();
        return ((200 <= statusCode) && (statusCode <= 299));
    }
}
