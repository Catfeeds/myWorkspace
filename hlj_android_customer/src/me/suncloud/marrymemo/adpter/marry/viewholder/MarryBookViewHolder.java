package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.marry.MarryBookImageAdapter;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.MarryBook;

/**
 * Created by hua_rong on 2017/12/8
 */
public class MarryBookViewHolder extends BaseViewHolder<MarryBook> {

    @BindView(R.id.iv_cover)
    RoundedImageView ivCover;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int width;
    private int height;
    private Context context;
    private OnItemClickListener<MarryBook> onItemClickListener;
    private MarryBookImageAdapter imageAdapter;


    public MarryBookViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        width = CommonUtil.dp2px(itemView.getContext(), 40);
        height = CommonUtil.dp2px(itemView.getContext(), 40);
    }

    @Override
    protected void setViewData(
            Context context, MarryBook marryBook, int position, int viewType) {
        if (marryBook != null) {
            tvMoney.setText(String.format(Locale.getDefault(), "￥%.2f", marryBook.getMoney()));
            String remark = marryBook.getRemark();
            String title = marryBook.getType()
                    .getTitle();
            DateTime dateTime = marryBook.getCreatedAt();
            String time = "";
            if (dateTime != null) {
                time = dateTime.toString(context.getString(R.string.format_date_type4));
            }
            if (marryBook.getTypeId() == MarryApi.TYPE_GIFT_ID) {
                tvName.setText(title);
                tvTime.setText(String.format(Locale.getDefault(),
                        "%1$s %2$s",
                        time,
                        TextUtils.isEmpty(remark) ? "无备注" : remark));
                tvMoney.setTextColor(Color.parseColor("#02ca5b"));
            } else {
                tvMoney.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                tvName.setText(TextUtils.isEmpty(remark) ? title : remark);
                tvTime.setText(String.format(Locale.getDefault(), "%1$s %2$s", time, title));
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(marryBook.getType()
                            .getSelectedImagePath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(ivCover);
            viewLine.setVisibility(marryBook.isFirstLine() ? View.GONE : View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getItemPosition(), getItem());
                    }
                }
            });
            setPhotos(marryBook.getImages());
        }
    }

    public void setOnItemClickListener(OnItemClickListener<MarryBook> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setPhotos(List<Photo> photos) {
        if (photos != null && photos.size() > 0) {
            if (imageAdapter == null) {
                imageAdapter = new MarryBookImageAdapter(context, photos);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(layoutManager);
                CompereItemDecoration decoration = new CompereItemDecoration(context);
                recyclerView.addItemDecoration(decoration);
                recyclerView.setAdapter(imageAdapter);
                recyclerView.setHasFixedSize(true);
            } else {
                imageAdapter.setPhotos(photos);
            }
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private class CompereItemDecoration extends RecyclerView.ItemDecoration {

        private int right;

        CompereItemDecoration(Context context) {
            right = CommonUtil.dp2px(context, 6);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = right;
        }
    }

}
