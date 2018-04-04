package com.hunliji.marrybiz.view.merchantservice;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.merchantservice.MerchantServerAdapter;
import com.hunliji.marrybiz.model.merchantservice.MarketItem;
import com.hunliji.marrybiz.model.orders.BdProduct;
import com.hunliji.marrybiz.util.MerchantServerUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2018/1/31.服务列表
 */

public class MerchantUltimateServerListActivity extends HljBaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<MarketItem> marketItems;
    private MerchantServerAdapter adapter;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_ultimate_server_list);
        ButterKnife.bind(this);
        initValue();
        initView();
    }

    private void initValue() {
        marketItems = MerchantServerUtil.getInstance()
                .getMarketItemFromFile(this);
        for (int i = 0; i < marketItems.size(); i++) {
            MarketItem marketItem = marketItems.get(i);
            if (marketItem.getProductId() == BdProduct.QI_JIAN_BAN || marketItem.getProductId()
                    == BdProduct.BAO_ZHENG_JIN || marketItem.getProductId() == BdProduct
                    .ZHUAN_YE_BAN || marketItem.getProductId() == BdProduct.WEDDING_WALL ||
                    marketItem.getProductId() == BdProduct.XIAO_CHENG_XU || marketItem
                    .getProductId() == BdProduct.YUN_KE) {
                marketItems.remove(marketItem);
                i--;
            }
        }
        headView = View.inflate(this, R.layout.merchant_ultimate_server_header, null);
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MerchantServerAdapter(this);
        adapter.setMarketItems(marketItems);
        adapter.setHeaderView(headView);
        recyclerView.setAdapter(adapter);
    }
}
