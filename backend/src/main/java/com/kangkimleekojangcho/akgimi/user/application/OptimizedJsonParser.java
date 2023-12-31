package com.kangkimleekojangcho.akgimi.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.kangkimleekojangcho.akgimi.global.exception.ServerErrorException;
import com.kangkimleekojangcho.akgimi.global.exception.ServerErrorExceptionCode;
import io.micrometer.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OptimizedJsonParser {
    public static String getObject(String json, String key) {
        JsonElement jsonElement = com.google.gson.JsonParser.parseString(json);
        return jsonElement.getAsJsonObject().get(key).toString();
    }

    public static <T> List<T> parse(String array, Class<T> klass) {
        if (StringUtils.isNotBlank(array)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readerForListOf(klass).readValue(array);
            } catch (JsonProcessingException e) {
                throw new ServerErrorException(ServerErrorExceptionCode.JSON_PARSE_ERROR);
            }
        }
        return new ArrayList<>();
    }
}
