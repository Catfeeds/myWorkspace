package me.suncloud.marrymemo.util;

import android.app.Activity;

import com.hunliji.hljcommonlibrary.models.Photo;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.model.JsonPic;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.task.QiNiuUploadTask;
import me.suncloud.marrymemo.widget.RoundProgressDialog;

/**
 * Created by werther on 16/5/24.
 * 批量上传图片列表的通用工具类
 */
public class PicListUploader {
    private Activity mContext;
    private OnFinishedListener mOnFinishedListener;
    private RoundProgressDialog mProgressDialog;
    private ArrayList<Photo> mPics;

    /**
     * 创建一个上传图片工具类的实例
     *
     * @param context            上传图片的上下文Activity实例
     * @param onFinishedListener 上传成功之后的回调接口,实现方法中返回的JsonPic列表中的图片路径即时网络地址
     * @param progressDialog     上传图片时的进度框
     * @param pics               上传的本地图片列表,JsonPic中的地址是本地图片的文件路径
     */
    public PicListUploader(
            Activity context, RoundProgressDialog progressDialog, ArrayList<Photo> pics,
            OnFinishedListener onFinishedListener) {
        this.mContext = context;
        this.mOnFinishedListener = onFinishedListener;
        this.mProgressDialog = progressDialog;
        this.mPics = pics;
    }

    /**
     * 开始上传图片
     */
    public void startUpload() {
        uploadPic(0);
    }

    private void uploadPic(int currentIndex) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            return;
        }
        if (currentIndex < mPics.size()) {
            Photo pic = mPics.get(currentIndex);
            if (!JSONUtil.isEmpty(pic.getImagePath()) && !pic.getImagePath()
                    .startsWith("http://")) {
                mProgressDialog.setMessage(currentIndex + 1 + "/" + mPics.size());
                new QiNiuUploadTask(mContext,
                        new ImageUploadRequestListener(pic),
                        mProgressDialog).executeOnExecutor(Constants.UPLOADTHEADPOOL,
                        Constants.getAbsUrl(Constants.HttpPath.QINIU_IMAGE_URL),
                        new File(pic.getImagePath()));
            } else {
                uploadPic(++currentIndex);
            }
        } else {
            if (mOnFinishedListener != null) {
                mOnFinishedListener.onFinish(mPics);
            }
        }

    }

    private class ImageUploadRequestListener implements OnHttpRequestListener {

        private Photo pic;

        private ImageUploadRequestListener(Photo pic) {
            this.pic = pic;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (mContext.isFinishing()) {
                return;
            }
            JSONObject json = (JSONObject) obj;
            String path = null;
            int width = 0;
            int height = 0;
            if (json != null) {
                String key = JSONUtil.getString(json, "image_path");
                String domain = JSONUtil.getString(json, "domain");
                width = json.optInt("width", 0);
                height = json.optInt("height", 0);
                if (!JSONUtil.isEmpty(key) && !JSONUtil.isEmpty(domain)) {
                    path = domain + key;
                }
            }
            if (!JSONUtil.isEmpty(path)) {
                int index = mPics.indexOf(pic);
                if (index >= 0) {
                    if (width > 0 && height > 0) {
                        pic.setWidth(width);
                        pic.setHeight(height);
                    }
                    pic.setImagePath(path);
                    uploadPic(++index);
                }
            } else {
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onRequestFailed(Object obj) {

        }
    }

    public interface OnFinishedListener {
        void onFinish(ArrayList<Photo> webPathPics);
    }
}
