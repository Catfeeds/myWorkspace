package com.hunliji.marrybiz.util;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljkefulibrary.HljKeFu;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.modulehelper.ModuleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author iDay
 */
public class Session {

    private MerchantUser currentUser;
    private Set<Long> cardIds = new HashSet<>();
    private DataConfig dataConfig;

    private Session() {
    }

    public static final Session getInstance() {
        return SessionHolder.INSTANCE;
    }


    public MerchantUser getCurrentUser() {
        return currentUser;
    }

    /**
     * @return the user
     */
    public MerchantUser getCurrentUser(Context context) {
        if (currentUser == null) {
            try {
                InputStream in = context.openFileInput(Constants.USER_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = -1;

                while ((length = in.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }

                currentUser = new MerchantUser(new JSONObject(stream.toString()));
                Log.e("User Info:", stream.toString());
                ModuleUtils.setUserToModules(context, currentUser);
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }

        return currentUser;
    }

    /**
     * @param user the user to set
     */
    public void setCurrentUser(MerchantUser user) {
        this.currentUser = user;
    }

    public void setCurrentUser(Context context, JSONObject user) {
        if (context == null) {
            return;
        }
        try {
            MerchantUser u = new MerchantUser(user);
            getCurrentUser(context);
            if (this.currentUser != null) {
                // 已登录用户修改用户数据的时候才会进入这里, 比如设置界面请求得到的用户数据会更新本地用户数据,但这个过程不会得到 Token,
                // 为确保本地用户数据中有 Token, 必须重填 Token
                if (user.isNull("token")) {
                    u.setToken(this.currentUser.getToken());
                    user.put("token", this.currentUser.getToken());
                }
                if (user.isNull("examine")) {
                    u.setExamine(this.currentUser.getExamine());
                    user.put("examine", this.currentUser.getExamine());
                }
                if (user.isNull("status")) {
                    u.setStatus(this.currentUser.getStatus());
                    user.put("status", this.currentUser.getStatus());
                }
                if (user.isNull("user_token")) {
                    u.setUserToken(this.currentUser.getUserToken());
                    user.put("user_token", this.currentUser.getUserToken());
                }
            }

            this.currentUser = u;

            FileOutputStream fileOutputStream = context.openFileOutput(Constants.USER_FILE,
                    Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(user.toString());
                out.flush();
                out.close();
                fileOutputStream.close();
            }
            getCurrentUser(context);

            // 有没有更好的办法?
            ModuleUtils.setUserToModules(context, currentUser);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setToken(Context context, String token) {
        if (context == null) {
            return;
        }
        if (currentUser != null) {
            currentUser.setToken(token);
        }
        if (context.getFileStreamPath(Constants.USER_FILE) != null && context.getFileStreamPath(
                Constants.USER_FILE)
                .exists()) {
            try {
                InputStream in = context.openFileInput(Constants.USER_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
                in.close();
                JSONObject user = new JSONObject(stream.toString());
                user.put("token", token);
                FileOutputStream fileOutputStream = context.openFileOutput(Constants.USER_FILE,
                        Context.MODE_PRIVATE);
                if (fileOutputStream != null) {
                    OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                    out.write(user.toString());
                    out.flush();
                    out.close();
                    fileOutputStream.close();
                }

                // 有没有更好的办法?
                ModuleUtils.setUserToModules(context, currentUser);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public MerchantUser editPhone(Context context, String phone) {
        if (context.getFileStreamPath(Constants.USER_FILE) != null && context.getFileStreamPath(
                Constants.USER_FILE)
                .exists()) {
            try {
                InputStream in = context.openFileInput(Constants.USER_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
                in.close();
                JSONObject user = new JSONObject(stream.toString());
                user.put("phone", phone);
                FileOutputStream fileOutputStream = context.openFileOutput(Constants.USER_FILE,
                        Context.MODE_PRIVATE);
                if (fileOutputStream != null) {
                    OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                    out.write(user.toString());
                    out.flush();
                    out.close();
                    fileOutputStream.close();
                }
                currentUser = new MerchantUser(user);

                // 有没有更好的办法?
                ModuleUtils.setUserToModules(context, currentUser);
                return currentUser;
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public void logout(Context context) {
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance()
                .startSync();
        CookieManager.getInstance()
                .removeAllCookie();
        WebSocket.getInstance().disconnect(context);

        context.deleteFile(Constants.USER_FILE);
        context.deleteFile(Constants.CARD_FILE);
        context.deleteFile(Constants.WEIBO_FILE);
        context.deleteFile(Constants.QQ_FILE);

        // 有没有更好的办法?
        ModuleUtils.logoutModules(context);
        HljKeFu.logout(context);
        this.currentUser = null;
    }

    private static class SessionHolder {
        private static final Session INSTANCE = new Session();
    }

    public DataConfig getDataConfig(Context context) {
        if (dataConfig == null) {
            if (context.getFileStreamPath(Constants.DATA_CONFIG_FILE) != null && context
                    .getFileStreamPath(
                    Constants.DATA_CONFIG_FILE)
                    .exists()) {
                try {
                    InputStream in = context.openFileInput(Constants.DATA_CONFIG_FILE);
                    String jsonStr = JSONUtil.readStreamToString(in);
                    JSONObject object = new JSONObject(jsonStr);
                    dataConfig = new DataConfig(object);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataConfig;
    }

    public void setDataConfig(Context context, JSONObject jsonObject) throws IOException {
        if (jsonObject == null) {
            return;
        }
        dataConfig = new DataConfig(jsonObject);
        FileOutputStream fileOutputStream = context.openFileOutput(Constants.DATA_CONFIG_FILE,
                Context.MODE_PRIVATE);
        if (fileOutputStream != null) {
            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
            out.write(jsonObject.toString());
            out.flush();
            out.close();
            fileOutputStream.close();
        }

    }
}
