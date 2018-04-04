package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaVipMerchant;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.chat.WSCustomerChatActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * Created by luohanlin on 2017/10/24.
 */

public class QaVipsAdapter extends RecyclerView.Adapter<BaseViewHolder<QaVipMerchant>> {

    private Context context;
    private List<QaVipMerchant> merchants;

    public static final String CPM_SOURCE = "qa_active_merchant";
    public static final int TYPE_ITEM = 1;

    public QaVipsAdapter(
            Context context, List<QaVipMerchant> merchants) {
        this.context = context;
        this.merchants = merchants;
    }

    @Override
    public BaseViewHolder<QaVipMerchant> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.qa_home_vip_item, parent, false);
        return new VipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<QaVipMerchant> holder, int position) {
        if (holder instanceof VipViewHolder) {
            holder.setView(context, merchants.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return merchants == null ? 0 : merchants.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    private abstract class TrackerVipMerchantViewHolder extends BaseViewHolder<QaVipMerchant> {

        public TrackerVipMerchantViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setView(
                Context mContext, QaVipMerchant item, int position, int viewType) {
            try {
                HljVTTagger.buildTagger(trackerView())
                        .tagName(HljTaggerName.MERCHANT)
                        .atPosition(position)
                        .dataId(item.getId())
                        .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_MERCHANT)
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_FLAG, item.getCpm())
                        .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, CPM_SOURCE)
                        .tag();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.setView(mContext, item, position, viewType);
        }

        public abstract View trackerView();
    }


    class VipViewHolder extends TrackerVipMerchantViewHolder {
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_merchant_property)
        TextView tvMerchantProperty;
        @BindView(R.id.tv_qa_count)
        TextView tvQaCount;
        @BindView(R.id.btn_contact_merchant)
        Button btnContactMerchant;
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.end_padding)
        View endPadding;

        VipViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            btnContactMerchant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AuthUtil.loginBindCheck(v.getContext())) {
                        return;
                    }
                    QaVipMerchant merchant = getItem();
                    if (merchant != null) {
                        Intent intent = new Intent(v.getContext(), WSCustomerChatActivity.class);
                        intent.putExtra(RouterPath.IntentPath.Customer.BaseWsChat
                                .ARG_USER_ID, merchant.getUserId());
                        v.getContext()
                                .startActivity(intent);
                    }
                }
            });
            HljVTTagger.buildTagger(btnContactMerchant)
                    .tagName(HljTaggerName.QA_MERCHANT_CHAT)
                    .hitTag();
        }

        @Override
        public View trackerView() {
            return imgAvatar;
        }

        @Override
        protected void setViewData(
                Context mContext, final QaVipMerchant merchant, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == merchants.size() - 1 ? View.VISIBLE : View.GONE);
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMerchant(merchant);
                }
            });

            Glide.with(context)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(CommonUtil.dp2px(context, 48))
                            .height(CommonUtil.dp2px(context, 48))
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgAvatar);
            tvName.setText(merchant.getName());
            tvQaCount.setText(merchant.getHotAnswerCount() > 0 ? mContext.getString(R.string
                            .label_vips_qa_counts,
                    String.valueOf(merchant.getHotAnswerCount())) : "");
            tvMerchantProperty.setText(mContext.getString(R.string.label_qa_merchant_property,
                    String.valueOf(merchant.getPropertyName())));
        }

        private void goToMerchant(QaVipMerchant merchant) {
            Intent intent = new Intent(context, MerchantDetailActivity.class);
            intent.putExtra("id", merchant.getId());
            context.startActivity(intent);
        }
    }
}
