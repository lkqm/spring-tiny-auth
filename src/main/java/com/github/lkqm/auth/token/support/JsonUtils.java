package com.github.lkqm.auth.token.support;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

}
