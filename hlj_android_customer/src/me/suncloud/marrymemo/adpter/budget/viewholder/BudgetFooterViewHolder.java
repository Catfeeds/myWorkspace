package me.suncloud.marrymemo.adpter.budget.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.wallet.FinancialHomeActivity;

/**
 * Created by jinxin on 2017/11/22 0022.
 */

public class BudgetFooterViewHolder extends BaseViewHolder {

    @BindView(R.id.footer_image_left)
    ImageView footerImageLeft;
    @BindView(R.id.footer_image_right)
    ImageView footerImageRight;
    @BindView(R.id.image_layout)
    LinearLayout imageLayout;

    private Context mContext;
    private City city;

    public BudgetFooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        city = Session.getInstance()
                .getMyCity(mContext);
    }

    @Override
    protected void setViewData(
            Context mContext, Object item, int position, int viewType) {

    }

    @OnClick(R.id.footer_image_left)
    void onClickLeft() {
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(mContext);
        if (dataConfig != null && !JSONUtil.isEmpty(dataConfig.getAdvIntroUrl())) {
            String advIntroUrl = dataConfig.getAdvIntroUrl();
            advIntroUrl = advIntroUrl + (advIntroUrl.contains("?") ? "&" : "?") + "city=" + (city
                    == null ? 0 : city.getId());
            Intent intent = new Intent(mContext, HljWebViewActivity.class);
            intent.putExtra("city", city);
            intent.putExtra("path", advIntroUrl);
            mContext.startActivity(intent);
        }
    }

    @OnClick(R.id.footer_image_right)
    void onClickRight() {
        Intent intent = new Intent(mContext, FinancialHomeActivity.class);
        mContext.startActivity(intent);
    }
}
