package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.community.WeddingBible;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;

/**
 * 宝典viewHolder
 * Created by chen_bin on 2018/3/16 0016.
 */
public class WeddingBibleViewHolder extends BaseViewHolder<WeddingBible> {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    public WeddingBibleViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wedding_bibles_item, parent, false));
    }

    public WeddingBibleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeddingBible bible = getItem();
                if (bible == null) {
                    return;
                }
                //设置当前浏览的宝典
                String json = GsonUtil.getGsonInstance()
                        .toJson(bible);
                SPUtils.put(v.getContext(), HljCommon.SharedPreferencesNames.PREF_LAST_BIBLE, json);

                Intent intent = new Intent(v.getContext(), CommunityThreadDetailActivity.class);
                intent.putExtra(CommunityThreadDetailActivity.ARG_ID, bible.getThreadId());
                v.getContext()
                        .startActivity(intent);
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingBible bible, int position, int viewType) {
        if (bible == null) {
            return;
        }
        try {
            HljVTTagger.buildTagger(itemView)
                    .tagName(HljTaggerName.WEDDING_BIBLE)
                    .atPosition(position)
                    .dataId(bible.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_WEDDING_BIBLE)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvTitle.setText(bible.getTitle());
    }
}
