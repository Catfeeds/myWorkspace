package me.suncloud.marrymemo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

/**
 * 标签页的header  因为要网路数据 单独写一个view
 * Created by jinxin on 2016/4/25.
 */
public class MarkHeaderView extends RelativeLayout {
    public static final int MARK = 1;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private MarkSpaceItemDecoration markSpaceItemDecoration;
    private MarkHeaderAdapter markHeaderAdapter;
    private long id;
    private FragmentActivity activity;
    private ArrayList<JSONObject> mData;
    private OnItemClickListener onItemClickListener;
    private OnDataChangeListener onDataChangeListener;
    private View paddingBottom;
    private Handler heightHandler;
    private static int height;
    private GetMarkTask task;

    public MarkHeaderView(Context context) {
        this(context, null);
    }

    public MarkHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.mark_activity_mark_header, this, true);
        init(context);
    }

    private void init(Context mContext) {
        mData = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.mark_list);
        manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        markSpaceItemDecoration = new MarkSpaceItemDecoration(mContext);
        markHeaderAdapter = new MarkHeaderAdapter(mContext);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(markSpaceItemDecoration);
        recyclerView.setAdapter(markHeaderAdapter);
        paddingBottom = findViewById(R.id.padding);
    }

    public void setContentVisible(boolean mark, boolean padding) {
        if (recyclerView != null) {
            recyclerView.setVisibility(mark?VISIBLE:GONE);
        }
        if (paddingBottom != null) {
            paddingBottom.setVisibility(padding?VISIBLE:GONE);
        }
    }

    public void setHeightHandler(Handler heightHandler) {
        this.heightHandler = heightHandler;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        if (heightHandler != null && task != null && task.getStatus() == AsyncTask.Status.FINISHED) {
            Message heightMsg = new Message();
            heightMsg.what = MARK;
            heightMsg.obj = height;
            heightHandler.sendMessage(heightMsg);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public static int getMarkHeight() {
        return height;
    }

    public void setRelativeId(long relativeId) {
        this.id = relativeId;
        task = new GetMarkTask();
        task.executeOnExecutor(Constants.LISTTHEADPOOL);
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void viewHolderNotify() {
        if (markHeaderAdapter != null) {
            markHeaderAdapter.notifyDataSetChanged();
        }
    }

    public void setPaddingVisible(boolean show) {
        if (paddingBottom != null) {
            paddingBottom.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    public ArrayList<JSONObject> getData() {
        return mData;
    }

    public class MarkSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public MarkSpaceItemDecoration(Context context) {
            this.space = Math.round(context.getResources().getDisplayMetrics().density * 10);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) < parent.getAdapter().getItemCount() - 1) {
                outRect.set(space, space, 0, space);
            } else {
                outRect.set(space, space, space, space);
            }
        }
    }

    public class MarkHeaderAdapter extends RecyclerView.Adapter<MarkHeaderViewHolder> {
        private Context mContext;
        private int imageWidth;

        public MarkHeaderAdapter(Context mContext) {
            this.mContext = mContext;
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            imageWidth = Math.round(dm.density * 80);
        }


        @Override
        public MarkHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View labelView = LayoutInflater.from(mContext).inflate(R.layout.mark_header_item, parent, false);
            return new MarkHeaderViewHolder(labelView);
        }

        @Override
        public void onBindViewHolder(MarkHeaderViewHolder holder, int position) {
            JSONObject image = mData.get(position);
            if (image != null) {
                String name = JSONUtil.getString(image, "name");
                String path = JSONUtil.getString(image, "image_path");
                if (!JSONUtil.isEmpty(path)) {
                    holder.itemView.setVisibility(VISIBLE);
                    String imagePath = JSONUtil.getImagePath(path, imageWidth);
                    ImageLoadTask task = new ImageLoadTask(holder.imageView);
                    holder.imageView.setTag(imagePath);
                    task.loadImage(imagePath, imageWidth, ScaleMode.WIDTH, new AsyncBitmapDrawable(getResources(), R
                            .drawable.image_mark_default, task));
                } else {
                    holder.imageView.setImageResource(R.drawable.image_mark_default);
                }
                if (!JSONUtil.isEmpty(name)) {
                    name = Util.getTextLength(name) > 5 ? name.substring(0, 5) + "..." : name;
                }
                holder.label.setText(name);
            }

            OnViewClickListener onViewClickListener = new OnViewClickListener(position);
            holder.itemView.setOnClickListener(onViewClickListener);
            holder.imageView.setOnClickListener(onViewClickListener);
            holder.label.setOnClickListener(onViewClickListener);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        class OnViewClickListener implements View.OnClickListener {
            private int position;

            public OnViewClickListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, mData.get(position), position);
                }
            }
        }
    }

    public class MarkHeaderViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView imageView;
        public TextView label;

        public MarkHeaderViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image);
            label = (TextView) itemView.findViewById(R.id.label);
        }
    }

    class GetMarkTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_MARK_LABEL_INFO, id);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            if (activity != null && activity.isFinishing()) {
                return;
            }

            if (object != null) {
                JSONArray data = object.optJSONArray("data");
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.optJSONObject(i);
                        if (item != null) {
                            mData.add(item);
                        }
                    }
                }
                markHeaderAdapter.notifyDataSetChanged();
                if (onDataChangeListener != null) {
                    onDataChangeListener.onDataChanged(mData, markHeaderAdapter, MarkHeaderView.this);
                }
            }
            super.onPostExecute(object);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, JSONObject data, int position);
    }

    public interface OnDataChangeListener {
        void onDataChanged(ArrayList<JSONObject> data, MarkHeaderAdapter adapter, ViewGroup parent);
    }

}
