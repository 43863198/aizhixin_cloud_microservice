package com.aizhixin.test.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class JsonUtil {
    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    /**
     * object to JSON File
     * @param file outFile
     * @param obj   object
     */
    public static void encode(File file, Object obj) {
        try {
            objectMapper.writeValue(file, obj);
//            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            LOG.error("encode(Object)", e);
        }
    }

    /**
     * object to JSON
     * @param obj   object
     * @return  Json string
     */
    public static String encode(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            LOG.error("encode(Object)", e);
        }
        return null;
    }

    /**
     * 将json string反序列化成对象
     *
     * @param json      json
     * @param valueType valueType
     * @return      Object
     */
    public static <T> T decode(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            LOG.error("decode(String, Class<T>)", e);
        }
        return null;
    }

    /**
     * 将json array反序列化为对象
     *
     * @param json  json
     * @param jsonTypeReference jsonTypeReference
     * @return  Object
     */
    public static <T> T decode(String json, TypeReference<T> jsonTypeReference) {
        try {
            return (T) objectMapper.readValue(json, jsonTypeReference);
        } catch (IOException e) {
            LOG.error("decode(String, JsonTypeReference<T>)", e);
        }
        return null;
    }

    /**
     * 将json array反序列化为对象
     *
     * @param file  file
     * @param jsonTypeReference jsonTypeReference
     * @return  Object
     */
    public static <T> T decode(File file, TypeReference<T> jsonTypeReference) {
        try {
            return (T) objectMapper.readValue(file, jsonTypeReference);
        } catch (IOException e) {
            LOG.error("decode(String, JsonTypeReference<T>)", e);
        }
        return null;
    }
}
