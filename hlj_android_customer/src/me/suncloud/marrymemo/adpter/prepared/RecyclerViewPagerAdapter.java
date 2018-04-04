package me.suncloud.marrymemo.adpter.prepared;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.prepared.WeddingPreparedCategoryMode;

/**
 * Created by Filippo-TheAppExpert on 9/15/2015.
 */
public abstract class RecyclerViewPagerAdapter extends RecyclerView
        .Adapter<RecyclerViewPagerAdapter.Holder> {
    private List<WeddingPreparedCategoryMode> categoryModeList;
    private int mWidth;
    private Context mContext;
    private int bottomMargin;

    public RecyclerViewPagerAdapter(Context mContext, List<WeddingPreparedCategoryMode> categoryModeList, int width) {
        this.mContext = mContext;
        this.categoryModeList =categoryModeList;
        mWidth = width;
        bottomMargin = Math.round(mContext.getResources()
                .getDisplayMetrics().density * 10);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View row = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_viewpager_item, viewGroup, false);
        ImageView imageView = (ImageView) row.findViewById(R.id.planetImage);
        //5/32
        imageView.getLayoutParams().width = imageView.getLayoutParams().height = mWidth * 5 / 32;
        RecyclerView.LayoutParams params;
        if (viewType == 0) {
            params = new RecyclerView.LayoutParams(mWidth * 5 / 32,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = mWidth / 2;
            params.bottomMargin = bottomMargin;
        } else if (viewType == 1) {
            params = new RecyclerView.LayoutParams(mWidth * 10 / 32,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.rightMargin = mWidth / 2;
            params.leftMargin = -mWidth * 11 / 128;
            params.bottomMargin = bottomMargin;
        } else {
            params = new RecyclerView.LayoutParams(mWidth * 5 / 32,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.bottomMargin = bottomMargin;
        }
        row.setLayoutParams(params);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        WeddingPreparedCategoryMode mode = categoryModeList.get(position);
        holder.mText.setText(mode.getName());
        holder.mImage.setImageResource(getImageRes(mode.getId()));
    }

    private int getImageRes(long categoryId) {
        int res = 0;
        if (categoryId == 1L) {
            res = R.drawable.icon_wedding_item5;
        } else if (categoryId == 2L) {
            res = R.drawable.icon_wedding_item3;
        } else if (categoryId == 3L) {
            res = R.drawable.icon_wedding_item2;
        } else if (categoryId == 4L) {
            res = R.drawable.icon_wedding_item4;
        } else if (categoryId == 5L) {
            res = R.drawable.icon_wedding_item1;
        } else if (categoryId == 6L) {
            res = R.drawable.icon_wedding_item6;
        }
        return res;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == (getItemCount() - 1)) {
            return 1;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return categoryModeList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImage;
        public TextView mText;

        public Holder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.planetImage);
            mText = (TextView) itemView.findViewById(R.id.planetName);
            itemView.setOnClickListener(Holder.this);
        }

        @Override
        public void onClick(View v) {
            snapViewToCenter(v);
        }
    }

    public abstract void snapViewToCenter(View view);
}
