package com.reedelk.esb.services.scriptengine.converter.integertype;

import com.reedelk.esb.services.scriptengine.DynamicValueConverter;

public class AsInteger implements DynamicValueConverter<Integer,Integer> {
    @Override
    public Integer to(Integer value) {
        return value;
    }
}
