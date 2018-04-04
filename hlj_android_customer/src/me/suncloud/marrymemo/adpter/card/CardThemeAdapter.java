package me.suncloud.marrymemo.adpter.card;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.ThemeV2;

/**
 * Created by wangtao on 2016/11/18.
 */

public class CardThemeAdapter extends RecyclerView.Adapter<BaseViewHolder<ThemeV2>> {

    private Context context;
    private List<ThemeV2> freeThemes;
    private List<ThemeV2> lockThemes;

    public static final int FREE_THEME_HEADER = -1;
    public static final int LOCK_THEME_HEADER = -2;
    public static final int THREE_THEME_ITEM = 1;
    private LongSparseArray<ThemeViewHolder> holderSparseLongArray;

    public enum ItemSpace {
        HEADER, Left, Right, Middle
    }

    private boolean isLock;

    private ThemeActionClickListener listener;

    public CardThemeAdapter(
            Context context, ThemeActionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setThemes(List<ThemeV2> freeThemes, List<ThemeV2> lockThemes) {
        this.freeThemes = freeThemes;
        this.lockThemes = lockThemes;
        notifyDataSetChanged();
    }

    public void clear() {
        if (freeThemes != null) {
            freeThemes.clear();
        }
        if (lockThemes != null) {
            lockThemes.clear();
        }
        notifyDataSetChanged();
    }

    public List<ThemeV2> getThemes() {
        List<ThemeV2> themes = new ArrayList<>();
        if (freeThemes != null) {
            themes.addAll(freeThemes);
        }
        if (lockThemes != null) {
            themes.addAll(lockThemes);
        }
        return themes;
    }

    /**
     * 更新解锁状态刷新界面
     *
     * @param lock
     */
    public void setLock(boolean lock) {
        isLock = lock;
        notifyDataSetChanged();
    }

    /**
     * 更新下载状态
     *
     * @param context
     * @param themeV2
     */
    public void notifyDataSetChanged(Context context, ThemeV2 themeV2) {
        if (freeThemes != null) {
            for (ThemeV2 theme : freeThemes) {
                if (theme.getId()
                        .equals(themeV2.getId())) {
                    theme.setDownLoading(themeV2.isDownLoading());
                    theme.setValue(themeV2.getValue());
                    if (themeV2.isSaved()) {
                        theme.onSaveCheck(context);
                    }
                    //                    int index = getPosition(themeV2);
                    //                    if (index >= 0) {
                    //                        notifyItemChanged(index);
                    //                    }
                    if (holderSparseLongArray != null && holderSparseLongArray.get(theme.getId())
                            != null) {
                        holderSparseLongArray.get(theme.getId())
                                .notifyProgressChanged(themeV2);
                    }
                    return;
                }
            }
        }
        if (lockThemes != null) {
            for (ThemeV2 theme : lockThemes) {
                if (theme.getId()
                        .equals(themeV2.getId())) {
                    theme.setDownLoading(themeV2.isDownLoading());
                    theme.setValue(themeV2.getValue());
                    if (themeV2.isSaved()) {
                        theme.onSaveCheck(context);
                    }
                    //                    int index = getPosition(themeV2);
                    //                    if (index >= 0) {
                    //                        notifyItemChanged(index);
                    //                    }
                    if (holderSparseLongArray != null && holderSparseLongArray.get(theme.getId())
                            != null) {
                        holderSparseLongArray.get(theme.getId())
                                .notifyProgressChanged(themeV2);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public BaseViewHolder<ThemeV2> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case THREE_THEME_ITEM:
                return new ThemeViewHolder(getView(parent, viewType));
        }
        return new ExtraViewHolder(getView(parent, viewType));
    }

    private View getView(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case THREE_THEME_ITEM:
                layoutId = R.layout.theme_v2_item;
                break;
            default:
                layoutId = R.layout.card_theme_header;
                break;
        }
        return LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ThemeV2> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    /**
     * 通过ThemeV2 定位position
     *
     * @param themeV2
     * @return 未匹配返回 -1
     */
    public int getPosition(ThemeV2 themeV2) {
        int position = 0;
        if (freeThemes != null) {
            if (freeThemes.contains(themeV2)) {
                position = freeThemes.indexOf(themeV2) + 1;
                return position;
            } else {
                position += freeThemes.size() + 1;
            }
        }
        if (lockThemes != null && lockThemes.contains(themeV2)) {
            position += lockThemes.indexOf(themeV2) + 1;
            return position;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (freeThemes != null && !freeThemes.isEmpty()) {
            count += freeThemes.size() + 1;
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            count += lockThemes.size() + 1;
        }
        return count;
    }

    private ThemeV2 getItem(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (position == 0) {
                return null;
            }
            position--;
            if (freeThemes.size() > position) {
                return freeThemes.get(position);
            }
            position -= freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return null;
            }
            position--;
            if (lockThemes.size() > position) {
                return lockThemes.get(position);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (position == 0) {
                return FREE_THEME_HEADER;
            }
            position--;
            if (freeThemes.size() > position) {
                return THREE_THEME_ITEM;
            }
            position -= freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return LOCK_THEME_HEADER;
            }
            position--;
            if (lockThemes.size() > position) {
                return THREE_THEME_ITEM;
            }
        }
        return 0;
    }

    public ItemSpace getSpaceType(int position) {
        if (freeThemes != null && !freeThemes.isEmpty()) {
            if (position == 0) {
                return ItemSpace.HEADER;
            }
            position--;
            if (freeThemes.size() > position) {
                switch (position % 3) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Middle;
                    case 2:
                        return ItemSpace.Right;
                }
            }
            position -= freeThemes.size();
        }
        if (lockThemes != null && !lockThemes.isEmpty()) {
            if (position == 0) {
                return ItemSpace.HEADER;
            }
            position--;
            if (lockThemes.size() > position) {
                switch (position % 3) {
                    case 0:
                        return ItemSpace.Left;
                    case 1:
                        return ItemSpace.Middle;
                    case 2:
                        return ItemSpace.Right;
                }
            }
        }
        return ItemSpace.HEADER;
    }

    private class ExtraViewHolder extends BaseViewHolder<ThemeV2> {

        private ImageView ivHeader;
        private ImageView ivLock;

        private ExtraViewHolder(View itemView) {
            super(itemView);
            ivHeader = (ImageView) itemView.findViewById(R.id.iv_header);
            ivLock = (ImageView) itemView.findViewById(R.id.iv_lock);
            ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLock && listener != null) {
                        listener.onUnlock();
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, ThemeV2 item, int position, int viewType) {
            switch (viewType) {
                case FREE_THEME_HEADER:
                    ivHeader.setVisibility(View.VISIBLE);
                    ivHeader.setImageResource(R.drawable.image_free_theme_header);
                    break;
                case LOCK_THEME_HEADER:
                    ivHeader.setVisibility(View.VISIBLE);
                    ivLock.setVisibility(View.VISIBLE);
                    ivHeader.setImageResource(R.drawable.image_lock_theme_header);
                    ivLock.setImageResource(isLock ? R.drawable.icon_lock_text_primary : R
                            .drawable.icon_unlock_text_primary);
                    break;
                default:
                    ivHeader.setVisibility(View.GONE);
                    ivLock.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public class ThemeViewHolder extends BaseViewHolder<ThemeV2> {

        @BindView(R.id.iv_thumb)
        ImageView ivThumb;
        @BindView(R.id.iv_new)
        ImageView ivNew;
        @BindView(R.id.progressbar)
        ProgressBar progressbar;
        @BindView(R.id.progressbar_layout)
        LinearLayout progressbarLayout;
        @BindView(R.id.theme_layout)
        RelativeLayout themeLayout;
        @BindView(R.id.btn_download)
        Button btnDownload;
        @BindView(R.id.iv_lock)
        ImageView ivLock;
        private int imageWidth;
        private int height;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            imageWidth = Math.round((CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context,
                    64)) / 3);
            height = Math.round((imageWidth * 122) / 75 + CommonUtil.dp2px(context, 1));
            themeLayout.getLayoutParams().height = height;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null || getItem() == null) {
                        return;
                    }
                    if (getItem().isLocked() && isLock) {
                        listener.onUnlock();
                    } else {
                        listener.onPreview(getItem());
                    }
                }
            });
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null || getItem() == null) {
                        return;
                    }
                    if (getItem().isLocked() && isLock) {
                        listener.onUnlock();
                    } else if (getItem().isSaved()) {
                        listener.onCreate(getItem());
                    } else {
                        listener.onDownLoad(getItem());
                    }

                }
            });
        }

        public void notifyProgressChanged(ThemeV2 themeV2) {
            ivLock.setVisibility(themeV2.isLocked() && isLock ? View.VISIBLE : View.GONE);
            if (themeV2.isSaved()) {
                if (progressbarLayout.getVisibility() == View.VISIBLE) {
                    progressbarLayout.setVisibility(View.GONE);
                }
                btnDownload.setText(R.string.btn_use);
            } else {
                btnDownload.setText(R.string.label_download);
                if (themeV2.isDownLoading()) {
                    if (progressbarLayout.getVisibility() != View.VISIBLE) {
                        progressbarLayout.setVisibility(View.VISIBLE);
                    }
                    progressbar.setProgress(themeV2.getValue());
                } else if (progressbarLayout.getVisibility() == View.VISIBLE) {
                    progressbarLayout.setVisibility(View.GONE);
                }

            }

        }

        @Override
        protected void setViewData(Context mContext, ThemeV2 themeV2, int position, int viewType) {
            if (holderSparseLongArray == null) {
                holderSparseLongArray = new LongSparseArray<>();
            }
            int index = holderSparseLongArray.indexOfValue(this);
            if (index >= 0) {
                holderSparseLongArray.removeAt(index);
            }
            holderSparseLongArray.put(themeV2.getId(), this);
            notifyProgressChanged(themeV2);
            String path = ImageUtil.getImagePath(themeV2.getThumbPath(), imageWidth);
            Glide.with(ivThumb.getContext())
                    .load(path)
                    .apply(new RequestOptions().dontAnimate())
                    .into(ivThumb);
        }
    }

    public interface ThemeActionClickListener {
        void onDownLoad(ThemeV2 themeV2);

        void onUnlock();

        void onCreate(ThemeV2 themeV2);

        void onPreview(ThemeV2 themeV2);
    }
}
