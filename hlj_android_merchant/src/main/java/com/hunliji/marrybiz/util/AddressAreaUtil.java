package com.hunliji.marrybiz.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.AddressArea;
import com.hunliji.marrybiz.model.ReturnStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by werther on 15/12/4.
 * 专门用于省市区数据的同步和解析的帮助类
 * 解析比较耗费时间,需要异步操作
 */
public class AddressAreaUtil {

    private static JSONArray addressAreaArray;
    private static ArrayList<AddressArea> areaLists;
    private static LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> areaMap;
    private static List<String> l1;
    private static List<List<String>> l2;
    private static List<List<List<String>>> l3;


    /**
     * 从以存储的文件中读取地址区域文件
     */
    static void getAddressAreaData(Context mContext) {
        try {
            if (mContext.getFileStreamPath(Constants.ADDRESS_AREA_FILE) != null && mContext
                    .getFileStreamPath(Constants.ADDRESS_AREA_FILE).exists()) {
                InputStream inputStream = mContext.openFileInput(Constants.ADDRESS_AREA_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }

                stream.flush();
                addressAreaArray = new JSONArray(stream.toString());
            } else {
                addressAreaArray = new JSONArray(JSONUtil.readStreamToString(mContext
                        .getResources().openRawResource(R.raw.address_area)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class GetAddressAreaDataTask extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;
        private OnFinishListener onFinishListener;

        public GetAddressAreaDataTask(Context mContext, OnFinishListener onFinishListener) {
            this.mContext = mContext;
            this.onFinishListener = onFinishListener;
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            if (addressAreaArray == null || addressAreaArray.length() == 0) {
                // 从本地存储中获取文件
                getAddressAreaData(mContext);
            }
            if (addressAreaArray == null || addressAreaArray.length() == 0) {
                // 如果本地存储中没有的话,从网络同步,第一次本地没有,肯定会从网络同步
                String url = Constants.getAbsUrl(Constants.HttpPath.ADDRESS_AREA_LIST);
                JSONObject resultObject = null;
                try {
                    String json = JSONUtil.getStringFromUrl(mContext, url);
                    if(JSONUtil.isEmpty(json)){
                        return null;
                    }
                    resultObject = new JSONObject(json);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                if (resultObject != null) {
                    ReturnStatus returnStatus = new ReturnStatus(resultObject.optJSONObject
                            ("status"));
                    if (returnStatus.getRetCode() == 0) {
                        addressAreaArray = resultObject.optJSONArray("data");
                    }
                }
            }

            // 解析地址信息
            parseAddressArea();

            return addressAreaArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if (onFinishListener != null) {
                onFinishListener.onFinish(areaLists, areaMap);
                onFinishListener.onFinish(l1, l2, l3);
            }

            if (jsonArray != null && jsonArray.length() > 0) {
                // 存储地区文件
                try {
                    FileOutputStream fileOutputStream = mContext.openFileOutput(Constants
                            .ADDRESS_AREA_FILE, Context.MODE_PRIVATE);
                    if (fileOutputStream != null) {
                        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                        writer.write(addressAreaArray.toString());
                        writer.flush();
                        writer.close();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonArray);
        }
    }

    public static class SyncAddressAreaTask extends AsyncTask<String, Integer, JSONObject> {

        private Context mContext;

        public SyncAddressAreaTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.ADDRESS_AREA_LIST);
            try {
                String json = JSONUtil.getStringFromUrl(mContext, url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    addressAreaArray = jsonObject.optJSONArray("data");
                    // 存储地区文件
                    try {
                        FileOutputStream fileOutputStream = mContext.openFileOutput(Constants
                                .ADDRESS_AREA_FILE, Context.MODE_PRIVATE);
                        if (fileOutputStream != null) {
                            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                            writer.write(addressAreaArray.toString());
                            writer.flush();
                            writer.close();
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    public interface OnFinishListener {
        void onFinish(ArrayList<AddressArea> al, LinkedHashMap<String, LinkedHashMap<String,
                ArrayList<String>>> am);

        void onFinish(List<String> l1, List<List<String>> l2, List<List<List<String>>> l3);
    }


    private static void parseAddressArea() {
        if (addressAreaArray != null && addressAreaArray.length() > 0) {
            ArrayList<AddressArea> tempAreaLists = new ArrayList<>();
            List<String> tempL1=new ArrayList<>();
            List<List<String>> tempL2=new ArrayList<>();
            List<List<List<String>>> tempL3=new ArrayList<>();
            LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> tempMap = new LinkedHashMap<>();
            for (int i = 0; i < addressAreaArray.length(); i++) {
                AddressArea area = new AddressArea(addressAreaArray.optJSONObject(i));
                if(!area.getChildren().isEmpty()) {
                    tempAreaLists.add(area);
                    tempL1.add(area.getAreaName());
                    tempL2.add(area.getChildrenNameList());
                    List<List<String>> tempL = new ArrayList<>();
                    LinkedHashMap<String, ArrayList<String>> secondLevelMap = new LinkedHashMap<>();
                    for(AddressArea child:area.getChildren()) {
                        tempL.add(child.getChildrenNameList());
                        secondLevelMap.put(child.getAreaName(),child.getChildrenNameList());
                    }
                    tempL3.add(tempL);
                    tempMap.put(area.getAreaName(), secondLevelMap);
                }
            }
            areaLists=new ArrayList<>(tempAreaLists);
            l1=new ArrayList<>(tempL1);
            l2=new ArrayList<>(tempL2);
            l3=new ArrayList<>(tempL3);
            areaMap=new LinkedHashMap<>(tempMap);
        }
    }

    public static AddressArea[] convertAreaIdToObj(Context mContext, long id) {
        if (areaLists == null || areaLists.isEmpty()) {
            getAddressAreaData(mContext);
            parseAddressArea();
        }
        for (AddressArea province : areaLists) {
            if (province.getChildren().size() > 0) {
                for (AddressArea city : province.getChildren()) {
                    AddressArea selectedProvince;
                    AddressArea selectedCity;
                    if (city.getId().equals(id)) {
                        selectedProvince = province;
                        selectedCity = city;
                        return new AddressArea[]{selectedProvince, selectedCity};
                    } else if (city.getChildren().size() > 0) {
                        for (AddressArea area : city.getChildren()) {
                            if (area.getId().equals(id)) {
                                selectedProvince = province;
                                selectedCity = city;
                                AddressArea selectedArea = area;
                                return new AddressArea[]{selectedProvince, selectedCity,
                                        selectedArea};
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static int[] getIndexesFromArea(Context mContext, AddressArea selectedProvince,
                                           AddressArea selectedCity, @Nullable AddressArea
                                                   selectedArea) {
        int[] result = new int[]{0, 0, 0};
        if(selectedProvince==null||selectedCity==null){
            return result;
        }
        if (areaLists == null || areaLists.isEmpty()) {
            getAddressAreaData(mContext);
            parseAddressArea();
        }
        for(AddressArea province:areaLists){
            if (province.getId().equals(selectedProvince.getId())) {
                result[0] = areaLists.indexOf(province);
                for(AddressArea city:province.getChildren()){
                    if (city.getId().equals(selectedCity.getId())) {
                        result[1] = province.getChildren().indexOf(city);
                        if(selectedArea!=null) {
                            for (AddressArea area : city.getChildren()) {
                                if (area.getId().equals(selectedArea.getId())) {
                                    result[2] = city.getChildren().indexOf(area);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }

        return result;
    }
}
