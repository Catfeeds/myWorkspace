package me.suncloud.marrymemo.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.entity.ImageLoadProgressListener;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.model.V2.TemplateV2;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.view.PageV2ChoosePhotoActivity;
import me.suncloud.marrymemo.view.PageV2EditActivity;
import me.suncloud.marrymemo.view.TemplateV2ListActivity;

/**
 * Created by Suncloud on 2016/5/6.
 */
public class TemplateV2ListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<TemplateV2>, AdapterView.OnItemClickListener {

    @BindView(R.id.list_view)
    PullToRefreshGridView listView;
    private ArrayList<TemplateV2> templates;
    private ObjectBindAdapter<TemplateV2> adapter;
    private int height;
    private int imageWidth;
    private CardV2 card;
    private LongSparseArray<TemplateDownloadTask> templateDownloadTasks;
    private boolean userThemeLock;
    private Unbinder unbinder;

    public static TemplateV2ListFragment newInstance(ArrayList<TemplateV2> templates, CardV2 card) {
        TemplateV2ListFragment fragment = new TemplateV2ListFragment();
        Bundle args = new Bundle();
        args.putSerializable("templates", templates);
        args.putSerializable("card", card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageWidth = Math.round((point.x - 55 * dm.density) / 3);
        height = Math.round((imageWidth * 122) / 75 + dm.density);
        templates = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), templates, R.layout.theme_v2_item, this);
        userThemeLock = CardResourceUtil.getInstance()
                .isUserThemeLockV2();
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_template_v2_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            card = (CardV2) getArguments().getSerializable("card");
            if (templates == null || templates.isEmpty()) {
                ArrayList<TemplateV2> temp = (ArrayList<TemplateV2>) getArguments().getSerializable(
                        "templates");
                if (temp != null) {
                    templates.addAll(temp);
                }
            }
        }
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0 && params[0] instanceof Boolean) {
            userThemeLock = (boolean) params[0];
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TemplateV2 template = (TemplateV2) parent.getAdapter()
                .getItem(position);
        if (template != null) {
            if (template.isLocked() && userThemeLock) {
                if (getActivity() != null && getActivity() instanceof TemplateV2ListActivity) {
                    ((TemplateV2ListActivity) getActivity()).onShare();
                }
            } else if (template.isSaved()) {
                Intent intent;
                if (template.getImageHoles()
                        .isEmpty()) {
                    intent = new Intent(getActivity(), PageV2EditActivity.class);
                    CardPageV2 cardPage = new CardPageV2(new JSONObject());
                    cardPage.setTemplate(template);
                    intent.putExtra("cardPage", cardPage);
                } else {
                    intent = new Intent(getActivity(), PageV2ChoosePhotoActivity.class);
                    intent.putExtra("limit",
                            template.getImageHoles()
                                    .size());
                    intent.putExtra("template", template);
                }
                intent.putExtra("card", card);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);

            } else if (!template.isDownLoading()) {
                if (templateDownloadTasks == null) {
                    templateDownloadTasks = new LongSparseArray<>();
                }
                if (templateDownloadTasks.get(template.getId()) == null) {
                    TemplateDownloadTask themeDownloadTask = new TemplateDownloadTask(template);
                    templateDownloadTasks.put(template.getId(), themeDownloadTask);
                    themeDownloadTask.executeOnExecutor(Constants.THEADPOOL);
                }
            }
        }
    }

    @Override
    public void setViewValue(View view, TemplateV2 templateV2, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.btnDownload.setVisibility(View.GONE);
            holder.themeLayout.getLayoutParams().height = height;
        }
        if (templateV2.isSaved()) {
            holder.progressbarLayout.setVisibility(View.GONE);
            holder.ivDownload.setVisibility(View.GONE);
        } else if (templateV2.isDownLoading()) {
            holder.progressbarLayout.setVisibility(View.VISIBLE);
            holder.ivDownload.setVisibility(View.GONE);
            holder.progressbar.setProgress(templateV2.getValue());
        } else {
            holder.progressbarLayout.setVisibility(View.GONE);
            holder.ivDownload.setVisibility(View.VISIBLE);
        }
        holder.ivLock.setVisibility(templateV2.isLocked() && userThemeLock ? View.VISIBLE : View
                .GONE);
        String path = JSONUtil.getImagePath(templateV2.getThumbPath(), imageWidth);
        if (!JSONUtil.isEmpty(path)) {
            if (!path.equals(holder.ivThumb.getTag())) {
                ImageLoadTask task = new ImageLoadTask(holder.ivThumb, null, 0);
                holder.ivThumb.setTag(path);
                AsyncBitmapDrawable image = new AsyncBitmapDrawable(getResources(),
                        R.mipmap.icon_empty_image,
                        task);
                task.loadImage(path, imageWidth, ScaleMode.WIDTH, image);
            }
        } else {
            holder.ivThumb.setTag(null);
            holder.ivThumb.setImageBitmap(null);
        }
    }


    static class ViewHolder {
        @BindView(R.id.iv_thumb)
        ImageView ivThumb;
        @BindView(R.id.iv_new)
        ImageView ivNew;
        @BindView(R.id.progressbar)
        ProgressBar progressbar;
        @BindView(R.id.progressbar_layout)
        LinearLayout progressbarLayout;
        @BindView(R.id.iv_lock)
        ImageView ivLock;
        @BindView(R.id.iv_download)
        ImageView ivDownload;
        @BindView(R.id.theme_layout)
        RelativeLayout themeLayout;
        @BindView(R.id.btn_download)
        Button btnDownload;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class TemplateDownloadTask extends AsyncTask<String, Integer, Boolean> {

        private TemplateV2 template;
        private int intValue;

        private TemplateDownloadTask(TemplateV2 template) {
            this.template = template;
            this.template.setDownLoading(true);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            ArrayList<String> images = template.getImagePaths(getActivity());
            ArrayList<String> fonts = template.getFontPaths(getActivity());

            int size = images.size() + fonts.size();
            if (size > 0) {
                final int value = 100 / size;
                int i = 0;
                for (String string : images) {
                    if (getActivity() == null && !getActivity().isFinishing()) {
                        return false;
                    }
                    intValue = 100 * i / size;
                    File file = FileUtil.createThemeFile(getActivity(), string);
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
                    if (getActivity() == null && !getActivity().isFinishing()) {
                        return false;
                    }
                    intValue = 100 * i / size;
                    File file = FileUtil.createFontFile(getActivity(), string);
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
                publishProgress(100);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            template.setValue(values[0]);
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            template.onSaveCheck(getActivity());
            template.setDownLoading(false);
            adapter.notifyDataSetChanged();
            if (templateDownloadTasks != null && templateDownloadTasks.get(template.getId()) !=
                    null) {
                templateDownloadTasks.remove(template.getId());
            }
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            template.setDownLoading(false);
            adapter.notifyDataSetChanged();
            if (templateDownloadTasks != null && templateDownloadTasks.get(template.getId()) !=
                    null) {
                templateDownloadTasks.remove(template.getId());
            }
            super.onCancelled();
        }
    }

    @Override
    public void onDestroy() {
        if (templateDownloadTasks != null && templateDownloadTasks.size() > 0) {
            ArrayList<TemplateDownloadTask> tasks = new ArrayList<>();
            for (int i = 0, size = templateDownloadTasks.size(); i < size; i++) {
                tasks.add(templateDownloadTasks.valueAt(i));
            }
            templateDownloadTasks.clear();
            for (TemplateDownloadTask task : tasks) {
                task.cancel(true);
            }
        }
        super.onDestroy();
    }
}
