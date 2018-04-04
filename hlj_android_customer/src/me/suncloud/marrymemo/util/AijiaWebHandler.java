package me.suncloud.marrymemo.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.view.AijiaWebViewActivity;

/**
 * Created by werther on 16/3/1.
 */
public class AijiaWebHandler {

    private static final String[] PHONES_PROJECTION = new String[]{ContactsContract
            .CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    private AijiaWebViewActivity aijiaWebViewActivity;
    private String upImgCallBackName;
    private WebView mWebView;
    private String mPubKey;


    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 53;
    private static final int MAX_ENCRYPT_BLOCK2 = 117;


    public AijiaWebHandler(AijiaWebViewActivity activity, WebView webView) {
        mPubKey = Constants.getAijiaPubKey();
        aijiaWebViewActivity = activity;
        mWebView = webView;
    }

    /**
     * 爱家理财js调用上传图片接口
     */
    @JavascriptInterface
    public void upimg(String callBackFunName) {
        this.upImgCallBackName = callBackFunName;
        aijiaWebViewActivity.onUploadImg();
    }

    /**
     * 爱家理财js调用上传通讯录接口
     */
    @JavascriptInterface
    public void contact(String callBackFunName) {
        Log.d("AijiaWebHandler", callBackFunName);
        // 获取通讯录
        int status = 0;
        // 判断权限
        PackageManager pm = aijiaWebViewActivity.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android" +
                        ".permission.READ_CONTACTS",
                "me.suncloud.marrymemo"));

        JSONArray jsonArray = new JSONArray();
        if (permission) {
            ContentResolver cr = aijiaWebViewActivity.getContentResolver();
            Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONES_PROJECTION,
                    null,
                    null,
                    null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(1);
                    phoneNumber.replaceAll("\\s+", "");
                    if (!JSONUtil.isEmpty(phoneNumber)) {
                        String name = phoneCursor.getString(0);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("name", name);
                            jsonObject.put("phone", phoneNumber);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                phoneCursor.close();
            }
        } else {
            status = -1;
        }

        String data = "";
        if (jsonArray != null && jsonArray.length() > 0) {
            status = 1;
            try {
                data = jsonArray.toString();

                // 上传到婚礼纪服务器
                uploadContact(data);

                // RSA加密
                data = encryptByPublicKey(data, mPubKey, MAX_ENCRYPT_BLOCK, "");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final String js = "javascript:" + callBackFunName + "('" + status + "', '" + data + "');";
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(js);
            }
        });
    }

    /**
     * 启动新的web view页面
     */
    @JavascriptInterface
    public void turnpage(String newUrl) {

        try {
            newUrl = URLDecoder.decode(newUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(newUrl)) {
            return;
        }

        Intent intent = new Intent(aijiaWebViewActivity, AijiaWebViewActivity.class);
        intent.putExtra("path", newUrl);
        aijiaWebViewActivity.startActivityForResult(intent, Constants.RequestCode.AIJIA_WEB_VIEW);
        aijiaWebViewActivity.overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    /**
     * 关闭爱家分期页面
     */
    @JavascriptInterface
    public void close() {
        aijiaWebViewActivity.manualCloseWebView();
    }

    /**
     * webview调用关闭当前页面
     */
    @JavascriptInterface
    public void manualclose() {
        aijiaWebViewActivity.manualCloseWebView();
    }


    /**
     * 支付结果
     *
     * @param result    0: 失败 1: 成功
     * @param resultMsg
     */
    @JavascriptInterface
    public void payresult(int result, String resultMsg) {
        aijiaWebViewActivity.onPayResult(result, resultMsg);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * 回调上传图片js接口
     *
     * @param data 以Base64编码的压缩过的图片
     */
    public void invokeUpImgCallBack(String data) {
        // 上传图片到服务器
        uploadImage(data);

        final String js = "javascript:" + upImgCallBackName + "('data:image/jpg;base64," + data +
                "');";
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(js);
            }
        });
    }

    /**
     * 加密数据
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    private String encryptByPublicKey(
            String data,
            String pKey,
            int blockSize,
            String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, IOException {
        byte[] dataBytes = data.getBytes("UTF-8");
        byte[] keyBytes = Base64.decodeBase64(pKey.getBytes());

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher;
        if (JSONUtil.isEmpty(algorithm)) {
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        } else {
            cipher = Cipher.getInstance(algorithm);
        }
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > blockSize) {
                cache = cipher.doFinal(dataBytes, offSet, blockSize);
            } else {
                cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * blockSize;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        byte[] s = Base64.encodeBase64(encryptedData);
        String str = new String(s, "UTF-8");
        return str;
    }

    /**
     * 上传加密通讯录到婚礼纪服务器
     *
     * @param data
     */
    private void uploadContact(String data) {
        String enData = null;
        try {
            enData = encryptByPublicKey(data,
                    Constants.HLJ_PUB_KEY,
                    MAX_ENCRYPT_BLOCK2,
                    "RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!JSONUtil.isEmpty(enData)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contact_data", enData);
                postData(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传加密图片到婚礼纪服务器
     *
     * @param data
     */
    private void uploadImage(String data) {
        if (!JSONUtil.isEmpty(data)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("image_data", data);
                postData(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void postData(String jsonStr) {
        new StatusHttpPostTask(aijiaWebViewActivity, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                Log.d("upload credit info", String.valueOf(returnStatus.getRetCode()));
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                Log.d("upload credit info", String.valueOf(returnStatus.getErrorMsg()));
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.UPLOAD_CREDIT_INFO), jsonStr);
    }
}
