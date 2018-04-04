package com.hunliji.marrybiz.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonHelper {

    private static Gson gson = createWcfGson();

    public static <T> String ToJsonString(T t) {
        return gson.toJson(t, t.getClass());
    }

    public static <T> String ToJsonString(T t, Class<T> clazz) {
        return gson.toJson(t, clazz);
    }

    /*
     * Only support non-generic type.
     */
    public static <T> T FromJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

            t = gson.fromJson(jsonString, clazz);

        } catch (Exception e) {
            int i = 0;
        }

        return t;
    }

    /*
     * To return array.
     */
    public static <T> List<T> FromJson(String jsonString, Type listType) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

        List<T> list = gson.fromJson(jsonString, listType);

        return list;
    }

    public static Gson createWcfGson() {
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(Date.class, new WcfDateDeserializer());
        // gsonb.registerTypeAdapter(Location.class, new
        // WcfLocationDeserializer());

        Gson gson = gsonb.excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        return gson;
    }

    private static class WcfDateDeserializer implements JsonDeserializer<Date>,
            JsonSerializer<Date> {


        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext
                context) throws JsonParseException {

            Date resultDate = null;
            String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
            Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
            Matcher matcher = pattern.matcher(json.getAsJsonPrimitive().getAsString());
            if (matcher.matches()) {
                String result = matcher.replaceAll("$2");

                resultDate = new Date(new Long(result));
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    resultDate = dateFormat.parse(json.getAsJsonPrimitive().getAsString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return resultDate;
        }

        @Override
        public JsonElement serialize(Date date, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive("/Date(" + date.getTime() + ")/");
        }
    }

}