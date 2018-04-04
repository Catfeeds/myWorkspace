package me.suncloud.marrymemo.adpter.user.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.views.activities.CardListActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.user.UserDynamic;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import me.suncloud.marrymemo.view.marry.MarryTaskActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;


/**
 * 用户动态
 * Created by chen_bin on 2017/11/28 0028.
 */
public class UserDynamicViewHolder extends BaseViewHolder<UserDynamic> {

    @BindView(R.id.top_line_layout)
    View topLineLayout;
    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_desc)
    TextView tvDesc;

    public UserDynamicViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDynamic dynamic = getItem();
                if (dynamic == null) {
                    return;
                }
                UserDynamic.UserDynamicType type = UserDynamic.UserDynamicType.getType(dynamic
                        .getType());
                if (type == null) {
                    return;
                }
                Intent intent = null;
                switch (type) {
                    case TYPE_WEDDING_ACCOUNT:
                        intent = new Intent(v.getContext(), MarryBookActivity.class);
                        break;
                    case TYPE_RECORD_WEDDING_TASK:
                    case TYPE_COMPLETE_WEDDING_TASK:
                        intent = new Intent(v.getContext(), MarryTaskActivity.class);
                        break;
                    case TYPE_WORK:
                        intent = new Intent(v.getContext(), WorkActivity.class);
                        intent.putExtra(WorkActivity.ARG_ID, dynamic.getEntityId());
                        break;
                    case TYPE_CASE:
                        intent = new Intent(v.getContext(), CaseDetailActivity.class);
                        intent.putExtra(CaseDetailActivity.ARG_ID, dynamic.getEntityId());
                        break;
                    case TYPE_PRODUCT:
                    case TYPE_SHOPPING_CART:
                        intent = new Intent(v.getContext(), ShopProductDetailActivity.class);
                        intent.putExtra(ShopProductDetailActivity.ARG_ID, dynamic.getEntityId());
                        break;
                    case TYPE_NOTE:
                        intent = new Intent(v.getContext(), NoteDetailActivity.class);
                        intent.putExtra(NoteDetailActivity.ARG_NOTE_ID, dynamic.getEntityId());
                        break;
                    case TYPE_MERCHANT:
                        intent = new Intent(v.getContext(), MerchantDetailActivity.class);
                        intent.putExtra(MerchantDetailActivity.ARG_ID, dynamic.getEntityId());
                        break;
                    case TYPE_CARD:
                        intent = new Intent(v.getContext(), CardListActivity.class);
                        break;
                }
                if (intent != null) {
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, UserDynamic dynamic, int position, int viewType) {
        if (dynamic == null) {
            return;
        }
        UserDynamic.UserDynamicType type = UserDynamic.UserDynamicType.getType(dynamic.getType());
        imgCover.setImageResource(type == null ? R.mipmap.icon_empty_image : type.getDrawable());
        tvTime.setText(dynamic.getCreatedAt() == null ? null : HljTimeUtils.getShowTime(mContext,
                dynamic.getCreatedAt()));
        tvTitle.setText(dynamic.getTitle());
        tvDesc.setText(dynamic.getDesc());
    }

    public void setShowTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }
}