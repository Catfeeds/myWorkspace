package me.suncloud.marrymemo.view.merchant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.HotelHallAdapter;

/**
 * Created by wangtao on 2017/10/10.
 */

public class HotelHallActivity extends HljBaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Merchant merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_recycler_view___cm);
        ButterKnife.bind(this);
        merchant = getIntent().getParcelableExtra("merchant");
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(this));
        recyclerView.setAdapter(new HotelHallAdapter(this,
                merchant,
                new OnItemClickListener<HotelHall>() {

                    @Override
                    public void onItemClick(int position, HotelHall hotelHall) {
                        if (hotelHall != null && hotelHall.getId() > 0) {
                            Intent intent = new Intent(HotelHallActivity.this,
                                    HotelHallDetailActivity.class);
                            intent.putExtra("id", hotelHall.getId());
                            startActivityForResult(intent, Constants.RequestCode.HOTEL_COLLECT);
                        }
                    }
                }));
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, parent.getChildAdapterPosition(view) == 0 ? space : 0, 0, space);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.HOTEL_COLLECT:
                    if (data != null) {
                        merchant.setCollected(data.getBooleanExtra("is_followed",
                                merchant.isCollected()));
                        Intent intent = getIntent();
                        intent.putExtra("is_followed", merchant.isCollected());
                        setResult(RESULT_OK, intent);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
