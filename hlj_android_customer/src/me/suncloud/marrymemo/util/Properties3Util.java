package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.MerchantProperty;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class Properties3Util {
    public static ArrayList<MerchantProperty> getPropertiesFromFile(Context mContext) {
        ArrayList<MerchantProperty> properties = new ArrayList<>();
        JSONArray array = null;
        try {
            File file = mContext.getFileStreamPath(Constants.PROPERTIES3_FILE);
            if (file != null && file.exists()) {
                InputStream in = mContext.openFileInput(Constants.PROPERTIES3_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            }
            if (array == null || TextUtils.isEmpty(array.toString())) {
                array = new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.properties3)));
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        if (array != null && !TextUtils.isEmpty(array.toString())) {
            for (int i = 0, size = array.length(); i < size; i++) {
                MerchantProperty menuItem = new MerchantProperty(array.optJSONObject(i));
                if (menuItem.getId() > 0) {
                    properties.add(menuItem);
                }
            }
        }
        return properties;
    }

    public static class PropertiesSyncTask extends AsyncTask<Object, Integer,
            ArrayList<MerchantProperty>> {

        private Context context;
        private OnFinishedListener onFinishedListener;

        public PropertiesSyncTask(Context context, OnFinishedListener onFinishedListener) {
            this.context = context;
            this.onFinishedListener = onFinishedListener;
        }

        @Override
        protected ArrayList<MerchantProperty> doInBackground(Object... params) {
            ArrayList<MerchantProperty> properties = new ArrayList<>();
            try {
                String url = Constants.getAbsUrl(Constants.HttpPath.GET_CATEGORY_URL) + "?parent=0";
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject(json).optJSONObject("data");
                if (jsonObject != null) {
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (array != null && array.length() > 0) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject resObject = array.getJSONObject(i);
                            MerchantProperty property = new MerchantProperty(resObject);
                            JSONObject object = new JSONObject();
                            object.put("id", property.getId());
                            object.put("name", property.getName());
                            object.put("children", resObject.optJSONArray("children"));
                            jsonArray.put(i, object);
                            properties.add(property);
                        }
                        OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(
                                Constants.PROPERTIES3_FILE,
                                Context.MODE_PRIVATE));
                        out.write(jsonArray.toString());
                        out.flush();
                        out.close();
                        return properties;
                    }
                }
                return null;
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MerchantProperty> properties) {
            if (context == null || ((Activity) context).isFinishing()) {
                return;
            }
            if (properties == null || properties.isEmpty()) {
                return;
            }
            onFinishedListener.onFinish(properties);
            super.onPostExecute(properties);
        }
    }

    public interface OnFinishedListener {
        void onFinish(ArrayList<MerchantProperty> properties);
    }


    /**
     * @param mContext
     * @param readDefault 是否读取raw目录下的properties3
     * @return
     */
    public static Observable<List<ShopCategory>> getPropertiesObb(
            final Context mContext, final boolean readDefault) {
        return Observable.concatDelayError(ProductApi.getProductProperty(mContext,
                Constants.HttpPath.GET_SHOP_PRODUCT_CATEGORY,
                true), Observable.create(new Observable.OnSubscribe<List<ShopCategory>>() {
            @Override
            public void call(
                    Subscriber<? super List<ShopCategory>> subscriber) {
                ArrayList<ShopCategory> categories = new ArrayList<>();
                JsonElement jsonElement = null;
                try {
                    InputStream in = null;
                    File file = mContext.getFileStreamPath(Constants.PROPERTIES3_FILE);
                    if (file != null && file.exists()) {
                        in = mContext.openFileInput(Constants.PROPERTIES3_FILE);
                    } else {
                        if (readDefault) {
                            in = mContext.getResources()
                                    .openRawResource(R.raw.properties3);
                        }
                    }
                    if (in != null) {
                        String jsonStr = JSONUtil.readStreamToString(in);
                        if (!TextUtils.isEmpty(jsonStr)) {
                            jsonElement = new JsonParser().parse(jsonStr);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (jsonElement != null) {
                    for (ShopCategory shopCategory : GsonUtil.getGsonInstance()
                            .fromJson(jsonElement, ShopCategory[].class)) {
                        //空数据过滤
                        if (shopCategory.getId() > 0) {
                            categories.add(shopCategory);
                        }
                    }
                    subscriber.onNext(categories);
                }
                subscriber.onCompleted();
            }
        }))
                .filter(new Func1<List<ShopCategory>, Boolean>() {
                    @Override
                    public Boolean call(List<ShopCategory> shopCategories) {
                        return shopCategories != null;
                    }
                })
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
