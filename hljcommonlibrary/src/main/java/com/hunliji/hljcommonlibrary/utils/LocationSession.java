package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Location;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Suncloud on 2016/8/18.
 */
public class LocationSession {

    private Location location;
    private City city;

    private static class SingleHolder {
        private static final LocationSession INSTANCE = new LocationSession();
    }

    public static LocationSession getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void saveLocation(Context context, Location location) {
        if (location == null) {
            return;
        }
        this.location = location;
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(HljCommon.FileNames
                    .LOCATION_FILE,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(new Gson().toJson(location));
                out.flush();
                out.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(Context context) {
        if (location == null) {
            try {
                if (context.getFileStreamPath(HljCommon.FileNames.LOCATION_FILE) != null &&
                        context.getFileStreamPath(
                        HljCommon.FileNames.LOCATION_FILE)
                        .exists()) {
                    InputStream in = context.openFileInput(HljCommon.FileNames.LOCATION_FILE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                    location = new Gson().fromJson(stream.toString(), Location.class);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return location;
    }

    public void saveCity(Context context, City city) {
        if (city == null || (this.city != null && city.getCid() == this.city.getCid())) {
            return;
        }
        this.city = city;
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(HljCommon.FileNames
                    .CITY_FILE,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(new Gson().toJson(city));
                out.flush();
                out.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public City getCity(Context context) {
        if (city == null) {
            try {
                if (context.getFileStreamPath(HljCommon.FileNames.CITY_FILE) != null && context
                        .getFileStreamPath(
                        HljCommon.FileNames.CITY_FILE)
                        .exists()) {
                    InputStream in = context.openFileInput(HljCommon.FileNames.CITY_FILE);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (city == null || city.getCid() == 0) {
                city = new City();
                city.setName(context.getString(R.string.label_all_city___cm));
            }
        }
        return city;
    }

    public String getLocalString(@Nullable  Context context) {
        JsonObject localJson = new JsonObject();
        if(location==null&&context!=null){
            location=getLocation(context);
        }
        if(city==null&&context!=null){
            city=getCity(context);
        }
        if (location != null) {
            try {
                localJson.addProperty("gps_longitude", location.getLongitude());
                localJson.addProperty("gps_latitude", location.getLatitude());
                if (!TextUtils.isEmpty(location.getProvince())) {
                    localJson.addProperty("gps_province",
                            URLEncoder.encode(location.getProvince(), "UTF-8"));
                }
                if (!TextUtils.isEmpty(location.getCity())) {
                    localJson.addProperty("gps_city",
                            URLEncoder.encode(location.getCity(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        localJson.addProperty("expo_cid", city == null ? 0 : city.getCid());
        localJson.addProperty("community_cid", city == null ? 0 : city.getCid());
        return localJson.toString();
    }

}
