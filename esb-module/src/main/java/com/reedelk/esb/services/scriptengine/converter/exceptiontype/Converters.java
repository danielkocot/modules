package com.reedelk.esb.services.scriptengine.converter.exceptiontype;

import com.reedelk.esb.services.scriptengine.converter.ValueConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Converters {

    public static final Map<Class<?>, ValueConverter<?,?>> ALL;
    static {
        Map<Class<?>, ValueConverter<?, ?>> tmp = new HashMap<>();
        tmp.put(byte[].class, new AsByteArray());
        tmp.put(String.class, new AsString());
        ALL = Collections.unmodifiableMap(tmp);
    }

    private Converters() {
    }
}
