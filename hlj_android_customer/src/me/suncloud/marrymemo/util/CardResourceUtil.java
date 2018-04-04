package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.LongSparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.PageTextMenuAdapter;
import me.suncloud.marrymemo.entity.ImageLoadProgressListener;
import me.suncloud.marrymemo.model.Font;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.Music;
import me.suncloud.marrymemo.model.V2.ThemeV2;
import me.suncloud.marrymemo.task.OnHttpRequestListener;

/**
 * Created by Suncloud on 2015/11/11.
 */
public class CardResourceUtil {

    private WeakReference<Context> contextWeakReference;
    private WeakReference<PageTextMenuAdapter> fontAdapterWeakReference;
    private static CardResourceUtil INSTANCE;
    private ArrayList<ThemeV2> themes;
    private ArrayList<Music> musics;
    private boolean onMusicLoad;
    private boolean userThemeLockV2;
    private JSONObject pageMapJson;
    private ArrayList<Font> fonts;
    private GetFontsTask fontsTask;
    private GetThemeV2Task themeV2Task;
    private FontDownloadTask fontDownloadTask;
    private LongSparseArray<ThemeDownloadTask> themeDownloadTasks;
    private WeakReference<OnHttpRequestListener> themeListenerWeakReference;
    private long lockUserId;

    private CardResourceUtil() {
    }

    public static CardResourceUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardResourceUtil();
        }
        return INSTANCE;
    }

    private ArrayList<Font> getFontsFromFile(Context mContext) {
        ArrayList<Font> fonts = new ArrayList<>();
        JSONArray array = null;
        try {
            if (mContext.getFileStreamPath(Constants.FONTS_FILE) != null && mContext
                    .getFileStreamPath(
                    Constants.FONTS_FILE)
                    .exists()) {
                InputStream in = mContext.openFileInput(Constants.FONTS_FILE);
                String jsonStr = JSONUtil.readStreamToString(in);
                array = new JSONArray(jsonStr);
            } else {
                array = (new JSONArray(JSONUtil.readStreamToString(mContext.getResources()
                        .openRawResource(R.raw.fonts))));
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        if (array != null && array.length() > 0) {
            for (int i = 0, size = array.length(); i < size; i++) {
                Font font = new Font(array.optJSONObject(i));
                if (!JSONUtil.isEmpty(font.getUrl())) {
                    fonts.add(font);
                }
            }
        }
        return fonts;
    }

    private JSONObject getPageMapJson(Context mContext) {
        if (pageMapJson != null) {
            return pageMapJson;
        }
        try {
            if (mContext.getFileStreamPath(Constants.PAGE_KEY_MAP) != null && mContext
                    .getFileStreamPath(
                    Constants.PAGE_KEY_MAP)
                    .exists()) {
                InputStream in = mContext.openFileInput(Constants.PAGE_KEY_MAP);
                String jsonStr = JSONUtil.readStreamToString(in);
                return new JSONObject(jsonStr);
            }
        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public synchronized void addPageMapKey(Context mContext, long pageId, String pageKey) {
        if (pageMapJson == null) {
            pageMapJson = new JSONObject();
        }
        try {
            pageMapJson.put(String.valueOf(pageId), pageKey);
            OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput(Constants
                            .PAGE_KEY_MAP,
                    Context.MODE_PRIVATE));
            out.write(pageMapJson.toString());
            out.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getPageMapKey(Context mContext, long pageId) {
        if (pageMapJson == null) {
            pageMapJson = getPageMapJson(mContext);
        }
        return pageMapJson.optString(String.valueOf(pageId));
    }

    public ArrayList<ThemeV2> getThemes() {
        if (themes == null) {
            themes = new ArrayList<>();
        }
        return themes;
    }

    public ArrayList<Font> getFonts(Context context) {
        if (fonts == null) {
            fonts = getFontsFromFile(context);
        }
        return fonts;
    }


    public Font getFont(Context context, String fontName) {
        if (JSONUtil.isEmpty(fontName)) {
            return null;
        }
        if (fonts == null) {
            fonts = getFontsFromFile(context);
        }
        for (Font font : fonts) {
            if (fontName.equals(font.getName())) {
                return font;
            }
        }

        return null;
    }

    public ArrayList<Music> getMusics() {
        if (musics == null) {
            musics = new ArrayList<>();
        }
        return musics;
    }

    public boolean isUserThemeLockV2() {
        return userThemeLockV2;
    }

    public void setUserThemeLockV2(boolean userThemeLockV2) {
        this.userThemeLockV2 = userThemeLockV2;
    }

    public void executeMusicTask(Context context) {
        if (!onMusicLoad) {
            contextWeakReference = new WeakReference<>(context);
            new GetMusicsTask().executeOnExecutor(Constants.LISTTHEADPOOL);
        }

    }

    public long getLockUserId() {
        return lockUserId;
    }

    public void executeThemeV2Task(Context context) {
        if (themeV2Task == null) {
            contextWeakReference = new WeakReference<>(context);
            if (Session.getInstance()
                    .getCurrentUser(context) != null) {
                lockUserId = Session.getInstance()
                        .getCurrentUser(context)
                        .getId();
            }
            themeV2Task = new GetThemeV2Task();
            themeV2Task.executeOnExecutor(Constants.LISTTHEADPOOL);
        }

    }

    public void executeFontsTask(Context context) {
        if (fontsTask == null) {
            contextWeakReference = new WeakReference<>(context);
            fontsTask = new GetFontsTask();
            fontsTask.executeOnExecutor(Constants.LISTTHEADPOOL);
        }

    }

    private class GetThemeV2Task extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .THEME_V2_LIST));
                if (JSONUtil.isEmpty(jsonStr)) {
                    return false;
                }
                JSONObject dataObject = new JSONObject(jsonStr).optJSONObject("data");
                if (dataObject != null) {
                    JSONArray array = dataObject.optJSONArray("theme_list");
                    int size = array.length();
                    if (size > 0) {
                        getThemes().clear();
                        for (int i = 0; i < size; i++) {
                            ThemeV2 theme = new ThemeV2(array.optJSONObject(i));
                            getThemes().add(theme);
                        }
                    }
                    userThemeLockV2 = dataObject.optInt("user_theme_lock") > 0;
                    return true;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            themeV2Task = null;
            MessageEvent event = new MessageEvent(MessageEvent.EventType.CARD_THEMEV2_UPDATE_FLAG,
                    true);
            EventBus.getDefault()
                    .post(event);
            super.onPostExecute(aBoolean);
        }
    }

    private class GetMusicsTask extends AsyncTask<String, Object, Boolean> {

        private GetMusicsTask() {
            onMusicLoad = true;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .MUSICS_URL));
                if (JSONUtil.isEmpty(jsonStr)) {
                    return false;
                }
                JSONArray array = null;
                JSONObject jsonObject = new JSONObject(jsonStr).optJSONObject("data");
                if (jsonObject != null) {
                    array = jsonObject.optJSONArray("musics");
                }
                if (array != null && array.length() > 0) {
                    Context context = contextWeakReference != null ? contextWeakReference.get() :
                            null;
                    if (context != null) {
                        FileOutputStream fileOutputStream = context.openFileOutput(Constants
                                        .MUSICS_FILE,
                                Context.MODE_PRIVATE);
                        if (fileOutputStream != null) {
                            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                            out.write(array.toString());
                            out.flush();
                            out.close();
                            fileOutputStream.close();
                        }
                    }
                    getMusics().clear();
                    int size = array.length();
                    for (int i = 0; i < size; i++) {
                        getMusics().add(new Music(array.optJSONObject(i)));
                    }
                    return true;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            onMusicLoad = false;
            if (aBoolean != null && aBoolean) {
                MessageEvent event = new MessageEvent(MessageEvent.EventType
                        .CARD_MUSIC_TEMPLATE_UPDATE_FLAG,
                        true);
                EventBus.getDefault()
                        .post(event);
            }
            super.onPostExecute(aBoolean);
        }
    }

    //获取字体列表
    private class GetFontsTask extends AsyncTask<String, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String jsonStr = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .FONT_LIST_URL));
                if (JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                JSONArray array = new JSONObject(jsonStr).optJSONArray("data");
                if (array != null && array.length() > 0) {
                    Context context = contextWeakReference != null ? contextWeakReference.get() :
                            null;
                    if (context != null) {
                        FileOutputStream fileOutputStream = context.openFileOutput(Constants
                                        .FONTS_FILE,
                                Context.MODE_PRIVATE);
                        if (fileOutputStream != null) {
                            OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                            out.write(array.toString());
                            out.flush();
                            out.close();
                            fileOutputStream.close();
                        }
                    }
                    return array;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null) {
                if (fonts == null) {
                    fonts = new ArrayList<>();
                } else {
                    fonts.clear();
                }
                for (int i = 0, size = result.length(); i < size; i++) {
                    Font font = new Font(result.optJSONObject(i));
                    if (!JSONUtil.isEmpty(font.getUrl())) {
                        fonts.add(font);
                    }
                }
            }
            fontsTask = null;
            super.onPostExecute(result);
        }
    }

    public void executeFontDownLoad(Context context, Font font, PageTextMenuAdapter adapter) {
        contextWeakReference = new WeakReference<>(context);
        if (fontAdapterWeakReference == null || fontAdapterWeakReference.get() != adapter) {
            fontAdapterWeakReference = new WeakReference<>(adapter);
        }
        if (fontDownloadTask != null && !fontDownloadTask.fontEquals(font)) {
            fontDownloadTask.cancel(true);
            fontDownloadTask = null;
        }
        if (fontDownloadTask == null) {
            fontDownloadTask = new FontDownloadTask(font);
            fontDownloadTask.executeOnExecutor(Constants.THEADPOOL);
        }
    }

    public void removeFontAdapter(PageTextMenuAdapter adapter) {
        if (fontAdapterWeakReference != null && fontAdapterWeakReference.get() == adapter) {
            fontAdapterWeakReference.clear();
        }
    }

    //字体下载
    private class FontDownloadTask extends AsyncTask<String, Integer, Boolean> {

        private Font font;

        private FontDownloadTask(Font font) {
            this.font = font;
        }

        private boolean fontEquals(Font font) {
            return font.equals(this.font);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Context context = contextWeakReference != null ? contextWeakReference.get() : null;
            if (context == null) {
                return false;
            }
            File file = FileUtil.createFontFile(context, font.getUrl());
            if (!file.exists() || file.length() == 0) {
                try {
                    byte[] data = JSONUtil.getByteArrayFromUrl(font.getUrl(),
                            new ImageLoadProgressListener() {
                                long contentLength;
                                long transfereLength;
                                long time;

                                @Override
                                public void transferred(int transferedBytes, String url) {
                                    if (contentLength > 0) {
                                        transfereLength += transferedBytes;
                                        if (time + 300 < System.currentTimeMillis()) {
                                            time = System.currentTimeMillis();
                                            publishProgress((int) ((transfereLength * 100) /
                                                    contentLength));
                                        }
                                    }
                                }

                                @Override
                                public void setContentLength(long contentLength, String url) {
                                    this.contentLength = contentLength;
                                }
                            });
                    if (data == null) {
                        return false;
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(data);
                    out.flush();
                    out.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (fontAdapterWeakReference != null && fontAdapterWeakReference.get() != null) {
                fontAdapterWeakReference.get()
                        .onFontProgressUpdate(font, values[0]);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (fontAdapterWeakReference != null && fontAdapterWeakReference.get() != null) {
                fontAdapterWeakReference.get()
                        .onFontDownLoadDone(font);
            }
            if (fontDownloadTask == this) {
                fontDownloadTask = null;
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            font.setDownloading(false);
            if (fontAdapterWeakReference != null && fontAdapterWeakReference.get() != null) {
                fontAdapterWeakReference.get()
                        .onFontDownLoadCancelled(font);
            }
            super.onCancelled();
        }
    }

    public void setThemeDownloadListener(OnHttpRequestListener onHttpRequestListener) {
        themeListenerWeakReference = new WeakReference<>(onHttpRequestListener);
    }

    public void executeThemeDownLoad(Context context, ThemeV2 theme) {
        contextWeakReference = new WeakReference<>(context);
        if (themeDownloadTasks == null) {
            themeDownloadTasks = new LongSparseArray<>();
        }
        if (themeDownloadTasks.get(theme.getId()) == null) {
            ThemeDownloadTask themeDownloadTask = new ThemeDownloadTask(theme);
            themeDownloadTasks.put(theme.getId(), themeDownloadTask);
            themeDownloadTask.executeOnExecutor(Constants.THEADPOOL);
        }
    }

    public void removeThemeDownloadListener(OnHttpRequestListener onHttpRequestListener) {
        if (themeListenerWeakReference != null && themeListenerWeakReference.get() ==
                onHttpRequestListener) {
            themeListenerWeakReference.clear();
        }
    }

    //模板下载
    private class ThemeDownloadTask extends AsyncTask<String, Integer, Boolean> {

        private ThemeV2 theme;
        private int intValue;

        private ThemeDownloadTask(ThemeV2 theme) {
            this.theme = theme;
            this.theme.setDownLoading(true);
            if (themeListenerWeakReference != null && themeListenerWeakReference.get() != null) {
                themeListenerWeakReference.get()
                        .onRequestCompleted(theme);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            ArrayList<String> images = theme.getImages(contextWeakReference.get());
            ArrayList<String> fonts = theme.getFonts(contextWeakReference.get());

            int size = images.size() + fonts.size();
            if (size > 0) {
                final int value = 100 / size;
                int i = 0;
                for (String string : images) {
                    if (contextWeakReference.get() == null) {
                        return false;
                    }
                    intValue = 100 * i / size;
                    File file = FileUtil.createThemeFile(contextWeakReference.get(), string);
                    if (!file.exists() || file.length() == 0) {
                        try {
                            byte[] data = JSONUtil.getByteArrayFromUrl(string,
                                    new ImageLoadProgressListener() {
                                        long contentLength;
                                        long transfereLength;
                                        long time;

                                        @Override
                                        public void transferred(int transferedBytes, String url) {
                                            if (contentLength > 0) {
                                                transfereLength += transferedBytes;
                                                if (time + 300 < System.currentTimeMillis()) {
                                                    time = System.currentTimeMillis();
                                                    publishProgress((int) (intValue +
                                                            (transfereLength * value) /
                                                                    contentLength));
                                                }
                                            }
                                        }

                                        @Override
                                        public void setContentLength(
                                                long contentLength, String url) {
                                            this.contentLength = contentLength;
                                        }
                                    });
                            if (data == null) {
                                return false;
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(data);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    i++;
                }
                for (String string : fonts) {
                    if (contextWeakReference.get() == null) {
                        return false;
                    }
                    intValue = 100 * i / size;
                    File file = FileUtil.createFontFile(contextWeakReference.get(), string);
                    if (!file.exists() || file.length() == 0) {
                        try {
                            byte[] data = JSONUtil.getByteArrayFromUrl(string,
                                    new ImageLoadProgressListener() {
                                        long contentLength;
                                        long transfereLength;
                                        long time;

                                        @Override
                                        public void transferred(int transferedBytes, String url) {
                                            if (contentLength > 0) {
                                                transfereLength += transferedBytes;
                                                if (time + 300 < System.currentTimeMillis()) {
                                                    time = System.currentTimeMillis();
                                                    publishProgress((int) (intValue +
                                                            (transfereLength * value) /
                                                                    contentLength));
                                                }
                                            }
                                        }

                                        @Override
                                        public void setContentLength(
                                                long contentLength, String url) {
                                            this.contentLength = contentLength;
                                        }
                                    });
                            if (data == null) {
                                return false;
                            }
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(data);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    i++;
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            theme.setValue(values[0]);
            if (themeListenerWeakReference != null && themeListenerWeakReference.get() != null) {
                themeListenerWeakReference.get()
                        .onRequestCompleted(theme);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            theme.onSaveCheck(contextWeakReference.get());
            theme.setDownLoading(false);
            if (themeListenerWeakReference != null && themeListenerWeakReference.get() != null) {
                if (aBoolean) {
                    themeListenerWeakReference.get()
                            .onRequestCompleted(theme);
                } else {
                    themeListenerWeakReference.get()
                            .onRequestFailed(theme);
                }
            }
            if (themeDownloadTasks != null && themeDownloadTasks.get(theme.getId()) != null) {
                themeDownloadTasks.remove(theme.getId());
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            theme.setDownLoading(false);
            if (themeListenerWeakReference != null && themeListenerWeakReference.get() != null) {
                themeListenerWeakReference.get()
                        .onRequestFailed(theme);
            }
            if (themeDownloadTasks != null && themeDownloadTasks.get(theme.getId()) != null) {
                themeDownloadTasks.remove(theme.getId());
            }
            super.onCancelled();
        }
    }
}
