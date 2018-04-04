package me.suncloud.marrymemo.adpter.work_case.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by wangtao on 2018/1/25.
 */

public class OldWorkRecWorkViewHolder extends TrackerWorkViewHolder {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_work_title)
    TextView tvWorkTitle;
    @BindView(R.id.tv_hot_tag)
    TextView tvHotTag;
    @BindView(R.id.tv_work_price)
    TextView tvWorkPrice;
    @BindView(R.id.tv_work_collect)
    TextView tvWorkCollect;
    @BindView(R.id.tv_work_price1)
    TextView tvWorkPrice1;

    @Override
    public String cpmSource() {
        return WorkActivity.CPM_SOURCE;
    }

    public OldWorkRecWorkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work=getItem();
                if(work!=null){
                    Intent intent = new Intent(v.getContext(), WorkActivity.class);
                    JSONObject jsonObject = TrackerUtil.getSiteJson("S3/A1",
                            getAdapterPosition() + 1,
                            "套餐" + work.getId() + work.getTitle());
                    if (jsonObject != null) {
                        intent.putExtra("site", jsonObject.toString());
                    }
                    intent.putExtra("id", work.getId());
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, Work work, int position, int viewType) {
        Glide.with(imgCover)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(CommonUtil.dp2px(imgCover.getContext(), 116))
                        .path())
                .into(imgCover);
        if (work.getHotTag() == 1 || work.getHotTag() == 2) {
            tvWorkTitle.setLineSpacing(0, 1f);
            tvWorkTitle.setMaxLines(1);
            tvHotTag.setVisibility(View.VISIBLE);
            tvHotTag.setText(work.getHotTag() == 1 ? R.string.label_work_rec : R.string
                    .label_work_top);
        } else {
            tvHotTag.setVisibility(View.GONE);
            tvWorkTitle.setLineSpacing(0, 1.3f);
            tvWorkTitle.setMaxLines(2);
        }
        tvWorkTitle.setText(work.getTitle());
        tvWorkPrice.setText(Util.formatDouble2String(work.getShowPrice()));
        tvWorkCollect.setText(tvWorkCollect.getContext().getString(R.string.label_collect_count,
                work.getCollectorsCount()));
        if (work.getMarketPrice() > 0) {
            tvWorkPrice1.setVisibility(View.VISIBLE);
            tvWorkPrice1.getPaint()
                    .setAntiAlias(true);
            tvWorkPrice1.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            tvWorkPrice1.setText(Util.formatDouble2String(work.getMarketPrice()));
        } else {
            tvWorkPrice1.setVisibility(View.GONE);
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }
}
