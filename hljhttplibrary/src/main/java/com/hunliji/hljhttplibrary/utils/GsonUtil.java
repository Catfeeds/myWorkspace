package com.hunliji.hljhttplibrary.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hunliji.hljhttplibrary.HljHttp;

import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by werther on 16/7/22.
 * Gson相关工具类
 * 提供Gson序列化/反序列化过程中的一些常用的方法
 */
public class GsonUtil {


    /**
     * 创建一个用于给Retrofit使用的解析joda time DateTime的converter
     *
     * @param dateFormatterStr json中时间值对应的格式,比如("yyyy-MM-dd HH:mm:ss")
     * @return
     */
    public static GsonConverterFactory buildHljCommonGsonConverter(String dateFormatterStr) {
        GsonBuilder gsonBuilder = initGsonBuilder();
        gsonBuilder.setDateFormat(dateFormatterStr);
        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

    private static Gson gsonInstance;


    /**
     * 获取全局的gson实例
     *
     * @return
     */
    public static Gson getGsonInstance() {
        if (gsonInstance == null) {
            gsonInstance = GsonUtil.buildHljCommonGson();
        }
        return gsonInstance;
    }

    /**
     * 创建一个用于给Retrofit使用的解析joda time DateTime等特殊格式的Gson
     *
     * @return
     */
    private static Gson buildHljCommonGson() {
        GsonBuilder gsonBuilder = initGsonBuilder();

        gsonBuilder.setDateFormat(HljHttp.TimeFormatPattern.PATTERN_1);

        return gsonBuilder.create();
    }

    private static GsonBuilder initGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());
        gsonBuilder.registerTypeAdapter(boolean.class, new BooleanSerializer());
        gsonBuilder.registerTypeAdapter(Boolean.class, new BooleanSerializer());
        gsonBuilder.registerTypeAdapter(String.class, new StringEmptySerializer());
        gsonBuilder.registerTypeAdapter(int.class, new IntegerFormatDeserializer());
        gsonBuilder.registerTypeAdapter(Integer.class, new IntegerFormatDeserializer());
        gsonBuilder.registerTypeAdapterFactory(new EmptyCheckTypeAdapterFactory());
        return gsonBuilder;
    }

    /**
     * 创建一个用于给Retrofit使用的解析多个特有格式的json的converter
     *
     * @param typeAdapters 一个model类和对应解析器的map, key是对应类, value是这个类的Deserializer实现
     * @return
     */
    public static GsonConverterFactory buildGsonConverter(
            Map<Class<?>, JsonDeserializer> typeAdapters) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Iterator iterator = typeAdapters.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Class cls = (Class) iterator.next();
            JsonDeserializer deserializer = typeAdapters.get(cls);
            gsonBuilder.registerTypeAdapter(cls, deserializer);
        }

        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

    /**
     * 创建一个用于给Retrofit使用的解析单个特有格式的json的converter
     *
     * @param cls          对应类
     * @param deserializer 这个类的Deserializer实现
     * @return
     */
    public static GsonConverterFactory buildGsonConverter(
            Class<?> cls, JsonDeserializer deserializer) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(cls, deserializer);

        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

    /**
     * 创建一个用于给Retrofit使用的解析单个特有格式的json的converter
     *
     * @param cls        对应类
     * @param serializer 这个类的JsonSerializer实现
     * @return
     */
    public static GsonConverterFactory buildGsonConverter(
            Class<?> cls, JsonSerializer serializer) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(cls, serializer);

        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

    /**
     * 解析DateTime的deserializer
     */
    private static class DateTimeTypeConverter implements JsonSerializer<DateTime>,
            JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
            return context.serialize(src.toDate(), Date.class);
        }

        @Override
        public DateTime deserialize(
                JsonElement json,
                Type type,
                JsonDeserializationContext context) throws JsonParseException {
            try {
                String timeStr = json.getAsString();
                if (TextUtils.isEmpty(timeStr) || timeStr.startsWith("0000-00-00")) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (json.getAsLong() > 0) {
                    long timeStamp = json.getAsLong();
                    if (String.valueOf(timeStamp)
                            .length() == 10) {
                        timeStamp = timeStamp * 1000L;
                    }
                    return new DateTime(timeStamp);
                }
            } catch (Exception ignored) {
            }
            try {
                return new DateTime(json.getAsString());
            } catch (IllegalArgumentException e) {
                // 可能是java.util.Date
                Date date = null;
                try {
                    date = context.deserialize(json, Date.class);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (date == null || date.getTime() < 0) {
                    return null;
                }
                return new DateTime(date);
            }
        }
    }

    /**
     * 将1/0解析为true/false的解析器
     */
    private static class BooleanSerializer implements JsonSerializer<Boolean>,
            JsonDeserializer<Boolean> {

        @Override
        public JsonElement serialize(Boolean arg0, Type arg1, JsonSerializationContext arg2) {
            return new JsonPrimitive(arg0);
        }

        @Override
        public Boolean deserialize(
                JsonElement arg0,
                Type arg1,
                JsonDeserializationContext arg2) throws JsonParseException {
            try {
                if (TextUtils.isEmpty(arg0.getAsString())) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int tmp;
            try {
                tmp = arg0.getAsInt();
            } catch (Exception e) {
                return arg0.getAsBoolean();
            }

            return tmp == 1;
        }
    }


    private static class StringEmptySerializer implements JsonDeserializer<String> {
        @Override
        public String deserialize(
                JsonElement arg0,
                Type arg1,
                JsonDeserializationContext arg2) throws JsonParseException {
            try {
                String string = arg0.getAsString();
                if ("null".equalsIgnoreCase(string)) {
                    return null;
                } else {
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class EmptyCheckTypeAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
            if (type.getRawType() == Boolean.class || type.getRawType() == String.class) {
                return null;
            }
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new EmptyCheckTypeAdapter<>(delegate, type.getRawType()).nullSafe();
        }
    }


    private static class IntegerFormatDeserializer implements JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(
                JsonElement arg0,
                Type arg1,
                JsonDeserializationContext arg2) throws JsonParseException {
            int i = 0;
            try {
                i = arg0.getAsInt();
            } catch (Exception e) {
                try {
                    i = (int) arg0.getAsDouble();
                } catch (Exception e2) {
                    e.printStackTrace();
                    e2.printStackTrace();
                }
            }
            return i;
        }
    }

    private static class EmptyCheckTypeAdapter<T> extends TypeAdapter<T> {

        private final TypeAdapter<T> delegate;
        private Class<? super T> rawType;

        EmptyCheckTypeAdapter(
                final TypeAdapter<T> delegate, Class<? super T> rawType) {
            this.delegate = delegate;
            this.rawType = rawType;
        }

        @Override
        public void write(final JsonWriter out, final T value) throws IOException {
            this.delegate.write(out, value);
        }

        @Override
        public T read(final JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            T data;
            if (in.peek() == JsonToken.STRING) {
                String result = in.nextString();
                if (TextUtils.isEmpty(result) || "null".equalsIgnoreCase(result)) {
                    return null;
                }
                if (rawType == Date.class && result.startsWith("0000-00-00")) {
                    return null;
                }
                try {
                    data = this.delegate.fromJson("\"" + result + "\"");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                data = this.delegate.read(in);
            }
            try {
                if (data != null && data instanceof List) {
                    ((List) data).removeAll(Collections.singleton(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return data;
        }
    }
}
